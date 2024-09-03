package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import com.mckesson.cgat.pde.usermanagement.dto.UserStudyResponse;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserAbstractionSyncUtils {
    private final UserRepository userRepository;
    private final WebClient webClient;
    private static final String ADD_USER_TO_STUDY = "/study/user/assign";
    private static final Integer USER_BATCH_SIZE = 500;

    public UserAbstractionSyncUtils(UserRepository userRepository, WebClient.Builder webClientBuilder, @Value("${chart.abstraction.service.base.url}") String baseUrl) {
        this.userRepository = userRepository;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Async
    public Mono<ResponseEntity<String>> syncPatientswithChartAbstraction(String authToken) {
        Optional<List<User>> users = userRepository.findUsersByIsSyncedWithChartAbstraction(0);
        if (users.isPresent() && !users.get().isEmpty()) {
            List<User> addedUsers = users.get();
            return assignSavedUsersToStudy(addedUsers, authToken);
        } else {
            log.info("No users found for syncing with study");
            return Mono.just(ResponseEntity.ok("No users found for syncing with study"));
        }
    }
    public Mono<ResponseEntity<String>> assignSavedUsersToStudy(List<User> usersToUpdate, String authToken) {
        List<UserStudyResponse> addedUserStudy = usersToUpdate.stream()
                .map(user -> new UserStudyResponse(user.getId().toString(), user.getDisplayName()))
                .collect(Collectors.toList());

        List<Mono<ResponseEntity<String>>> responseMonos = new ArrayList<>();
        if (addedUserStudy.size() > USER_BATCH_SIZE) {
            for (int i = 0; i < addedUserStudy.size(); i += USER_BATCH_SIZE) {
                List<UserStudyResponse> batch = addedUserStudy.subList(i, Math.min(i + USER_BATCH_SIZE, addedUserStudy.size()));
                responseMonos.add(updateSyncWithChartAbstractionFlag(batch, authToken));
            }
        } else {
            responseMonos.add(updateSyncWithChartAbstractionFlag(addedUserStudy, authToken));
        }

        return Mono.when(responseMonos)
                .then(Mono.just(ResponseEntity.ok("Users assigned to study successfully.")))
                .onErrorMap(e -> {
                    log.error("Error assigning users to study.", e);
                    return new RuntimeException("Error assigning users to study.", e);
                });
    }
    private Mono<ResponseEntity<String>> updateSyncWithChartAbstractionFlag(List<UserStudyResponse> users, String authToken) {
        return saveUsersInUserStudy(users, authToken)
                .flatMap(apiResponse -> {
                    userRepository.updateIsSyncWithChartAbstraction(
                            users.stream().map(userStudyResponse -> UUID.fromString(userStudyResponse.getUserId())).collect(Collectors.toList()),
                            1);
                    return Mono.just(apiResponse);
                });
    }
    private Mono<ResponseEntity<String>> saveUsersInUserStudy(List<UserStudyResponse> users, String authToken) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(ADD_USER_TO_STUDY)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(users)
                .retrieve()
                .toBodilessEntity()
                .map(responseEntity -> {
                	HttpStatusCode status = responseEntity.getStatusCode();
                    if (status.is2xxSuccessful()) {
                        return ResponseEntity.ok("Users assigned to study successfully.");
                    } else {
                        return ResponseEntity.status(status).body("Failed to assign users to study.");
                    }
                })
                .onErrorMap(e -> {
                    log.error("Error connecting with abstraction service.", e);
                    return new RuntimeException("Error connecting with abstraction service.", e);
                });
    }
}

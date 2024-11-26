package com.mckesson.cgat.pde.usermanagement.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class WebClientUtils {

	@Value("${usermanagementservice.base.url}")
	private String userServiceBaseUrl;

	private final WebClient webClient;

	public WebClientUtils(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("").build();
	}

	public ResponseEntity<Flux<Object>> makeGetRequest(String uri) {

		return (ResponseEntity<Flux<Object>>) webClient.get()
			.uri(uri)
			.header(HttpHeaders.CONTENT_TYPE, "application/json")
			.header("userId", UserSecurityUtils.getLoggedInUser().getId())
			.retrieve()
			.toEntityFlux(Object.class)
			.map(responseEntity -> {
				HttpStatusCode statusCode = responseEntity.getStatusCode();
				if (statusCode.is2xxSuccessful()) {
                    return ResponseEntity.ok(responseEntity.getBody());
                }
                else {
                    return ResponseEntity.status(statusCode).body("");
                }
			}).onErrorMap(e -> {
                log.error("Error connecting with user management service.", e);
                return new RuntimeException("Error connecting with user management service.", e);
            })
			.block();

	}

}

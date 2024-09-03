package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.jbat.common.util.constant.UserRole;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.jbat.common.util.service.MessageResolver;
import com.mckesson.cgat.pde.usermanagement.constant.UserActivity;
import com.mckesson.cgat.pde.usermanagement.dto.UserResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UserRoleRequest;
import com.mckesson.cgat.pde.usermanagement.dto.UserStudyResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UsersRoleBulkUploadResponse;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.mapper.Transformer;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserActivityAudit;
import com.mckesson.cgat.pde.usermanagement.model.UserProfileAudit;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserActivityAuditRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserProfileAuditRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.UserAbstractionSyncUtils;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mckesson.cgat.pde.usermanagement.constant.ApplicationConstants.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Transformer transformer;
    private final UserAbstractionSyncUtils userAbstractionSyncUtils;
    private final UserProfileAuditRepository userProfileAuditRepository;
    private final UserActivityAuditRepository userActivityAuditRepository;

    private final String header1 = "Username";
    private final String header2 = "User email";
    private final String header3 = "Role";
    private final String header4 = "Status";

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, Transformer transformer, UserAbstractionSyncUtils userAbstractionSyncUtils,UserProfileAuditRepository userProfileAuditRepository, UserActivityAuditRepository userActivityAuditRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.transformer = transformer;
        this.userAbstractionSyncUtils = userAbstractionSyncUtils;
        this.userProfileAuditRepository = userProfileAuditRepository;
        this.userActivityAuditRepository = userActivityAuditRepository;
    }

    @Override
    @Transactional
    public User assignRole(String userId, String roleName) throws ResourceNotFoundException {
        log.info("Assigning role for userId : {}", userId);
        Optional<User> userOptional = userRepository.findById(UUID.fromString(userId));
        User user = userOptional.orElseThrow(() -> new ResourceNotFoundException("User Not Found. Id=" + userId));
        Optional<Role> roleOption = roleRepository.findByName(roleName);
        Role role = roleOption.orElseThrow(() -> new ResourceNotFoundException("Role Not Found. Name=" + roleName));
        String previousRole =  getUserRolesCommaSaperated(user);
        user.getRoles().clear();
        user.getRoles().add(role);
        user.setUpdatedDate(new Date());
        user =  userRepository.save(user);
        updateUserProfileAudit(userId,roleName,previousRole);
        return user;
    }

    @Override
    public Page<UserResponse> findAll(Pageable paging, String query, List<String> roles, List<String> status) {
        log.info("Searching user based on filter query:{},  roles:{}", query, roles);
        query = StringUtils.toRootLowerCase(query);
        Page<User> response;
        String statusActive = "ALL";
        if (!CollectionUtils.isEmpty(status)) {
            if (status.stream().anyMatch(i -> i.equalsIgnoreCase("ACTIVE"))) {
                statusActive = "ACTIVE";
            }
            if (status.stream().anyMatch(i -> i.equalsIgnoreCase("INACTIVE"))) {
                statusActive = "ACTIVE".equals(statusActive) ? "ALL" : "INACTIVE";
            }
        }
        if (CollectionUtils.isEmpty(roles)) {
            response = userRepository.findAllByQueryText(query, statusActive, paging);

        } else if (roles.contains(ROLE_UNASSIGNED)) {
            response = userRepository.findAllByQueryTextRoleUnAssigned(query, statusActive, paging);

        } else {
            response = userRepository.findAllByQueryTextAndRoles(query, roles, statusActive, paging);
        }
        return response.map(user -> transformer.toUserDto(user));
    }

    @Override
    public UsersRoleBulkUploadResponse assignRoleforAllUsers(MultipartFile uploadedCsvFile, boolean save, String authToken) {
        Map<String, Role> validRoles = roleRepository.findAll().stream()
                .collect(Collectors.toMap(Role::getLabel, Function.identity()));
        UsersRoleBulkUploadResponse usersRoleBulkUploadResponse = validateAndParseUserRoleCSV(uploadedCsvFile,
                validRoles);
        if (usersRoleBulkUploadResponse.isHasErrors())
            return usersRoleBulkUploadResponse;
        return assignRoleForAllUsers(usersRoleBulkUploadResponse.getRecords(), validRoles, save, authToken);
    }

    public UsersRoleBulkUploadResponse validateAndParseUserRoleCSV(MultipartFile uploadedCsvFile,
                                                                   Map<String, Role> validRoles) {
        UsersRoleBulkUploadResponse usersRoleBulkUploadResponse = new UsersRoleBulkUploadResponse();
        Set<String> emails = new HashSet<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(uploadedCsvFile.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            List<UserRoleRequest> userRoles = new ArrayList<>();
            List<CSVRecord> csvRecords = csvParser.getRecords();
            if (csvRecords.size() == 0 || csvRecords.size() == 1)
                throw new FileNotValidException(MessageResolver.resolve("userupload.message.blankFileMsg"));

            csvRecords.forEach(record -> {
                if (record.getRecordNumber() == 1) {
                    if (record.size() < 4 || !(record.get(0).trim().equals(header1) && record.get(1).trim().equals(header2)
                            && record.get(2).trim().equals(header3) && record.get(3).trim().equals(header4)))
                        throw new FileNotValidException(
                                MessageResolver.resolve("userupload.message.fileHeaderDiffMsg"));
                    return;
                }
                String displayName = record.get(0).trim();
                String email = StringUtils.toRootLowerCase(record.get(1).trim());
                String userRole = record.get(2).trim();
                String status = record.get(3).trim().toUpperCase();
                UserRoleRequest userRoleRequest = new UserRoleRequest(displayName, email,
                        Collections.singletonList(userRole), status);
                userRoleRequest.setValidationMessages(
                        validateUserRoleRequests(userRoles, userRoleRequest, validRoles, emails));
                if (userRoleRequest.getValidationMessages().size() > 0 && !usersRoleBulkUploadResponse.isHasErrors())
                    usersRoleBulkUploadResponse.setHasErrors(true);
                userRoles.add(userRoleRequest);
            });
            csvParser.close();
            usersRoleBulkUploadResponse.setRecords(userRoles);
            return usersRoleBulkUploadResponse;
        } catch (IOException e) {
            throw new RuntimeException(MessageResolver.resolve("userupload.message.csvParsingError"), e);
        }
    }

    @Transactional
    public UsersRoleBulkUploadResponse assignRoleForAllUsers(List<UserRoleRequest> userRoleMappings,
                                                              Map<String, Role> validRoles, boolean save, String authToken) {
        UsersRoleBulkUploadResponse response = new UsersRoleBulkUploadResponse();
        response.setHasErrors(false);
        List<User> usersToUpdate = new ArrayList<>();
        userRoleMappings.forEach(userRoleMapping -> {
            if (userRoleMapping.getValidationMessages().size() == 0) {
                List<Role> userRoleToUpdate = userRoleMapping.getRoles().stream().map(role -> {
                    return validRoles.get(role);
                }).collect(Collectors.toList());
                User user = userRepository.findByEmail(StringUtils.toRootLowerCase(userRoleMapping.getEmail()));
                if (user != null) {
                    userRoleMapping.getValidationMessages()
                            .add(MessageResolver.resolve("userupload.message.existingUserMsg"));
                    response.setHasErrors(true);
                } else {
                    User newUser = new User(StringUtils.toRootLowerCase(userRoleMapping.getEmail()), userRoleMapping.getDisplayName(),
                            userRoleMapping.getStatus().equalsIgnoreCase("ACTIVE"), 0, null, userRoleToUpdate);
                    usersToUpdate.add(newUser);
                    log.info("No user found for userId: {}", userRoleMapping.getEmail());
                }
            }
        });
        if (save && !response.isHasErrors()) {
            List<User> usersAdded = userRepository.saveAll(usersToUpdate);
            usersAdded.stream().forEach(u -> {
                updateUserProfileAudit(u.getId().toString(),getUserRolesCommaSaperated(u),null);
            });
        }
        Mono<Void> syncPatients = userAbstractionSyncUtils.syncPatientswithChartAbstraction(authToken)
                .doOnError(error -> {
                    log.error("Failed to sync patients with chart abstraction: {}", error.getMessage());
                })
                .onErrorResume(error -> Mono.empty()).then();
        syncPatients.subscribe();
        response.setRecords(userRoleMappings);
        return response;
    }

    private Set<String> validateUserRoleRequests(List<UserRoleRequest> userRoles, UserRoleRequest userRoleRequest,
                                                 Map<String, Role> validRoles, Set<String> emails) {
        Set<String> validationResults = new HashSet<>();
        if (StringUtils.isEmpty(userRoleRequest.getDisplayName())) {
            validationResults.add(MessageResolver.resolve("userupload.message.nameMissingMsg"));
        }
        if (StringUtils.isEmpty(userRoleRequest.getEmail())) {
            validationResults.add(MessageResolver.resolve("userupload.message.emailMissingMsg"));
        } else if (!CustomUtils.validateEmail(userRoleRequest.getEmail())) {
            validationResults.add(MessageResolver.resolve("userupload.message.emailInvalidMsg"));
        } else if (emails.add(userRoleRequest.getEmail()) == false) {
            validationResults.add(MessageResolver.resolve("userupload.message.duplicateUserMsg"));
            Optional<UserRoleRequest> userRoleRequestLocal = userRoles.stream()
                    .filter(x -> x.getEmail().trim().equalsIgnoreCase(userRoleRequest.getEmail().trim())).findFirst();
            if (userRoleRequestLocal.isPresent()) {
                userRoleRequestLocal.get().getValidationMessages()
                        .add(MessageResolver.resolve("userupload.message.duplicateUserMsg"));
            }
        }
        if (StringUtils.isEmpty(userRoleRequest.getStatus())) {
            validationResults.add(MessageResolver.resolve("userupload.message.statusMissingMsg"));
        } else if (!(userRoleRequest.getStatus().equals("ACTIVE") || userRoleRequest.getStatus().equals("INACTIVE"))) {
            validationResults.add(MessageResolver.resolve("userupload.message.statusInvalidMsg"));
        }
        // Assuming one role for a user for MVP
        if (userRoleRequest.getRoles().size() == 0
                || (userRoleRequest.getRoles().size() > 0 && StringUtils.isEmpty(userRoleRequest.getRoles().get(0)))) {
            validationResults.add(MessageResolver.resolve("userupload.message.roleMissingMsg"));
        } else if (!isValidRole(validRoles, userRoleRequest.getRoles())) {
            validationResults.add(MessageResolver.resolve("userupload.message.roleInvalidMsg"));
        }
        return validationResults;
    }

    private boolean isValidRole(Map<String, Role> validRoles, List<String> roles) {
        for (String role : roles) {
            if (validRoles.containsKey(role))
                return true;
        }
        return false;
    }


    @Override
    public User getUser(String userId) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        return user.orElseThrow(() -> new ResourceNotFoundException("User Not Found. Id=" + userId));
    }

    @Override
    public List<UserStudyResponse> getUsersBasedOnRole(UserRole role, List<UUID> userIds) {
        List<User> userRoles = userRepository.findUsersByRoles_NameAndIdIn(role.name(), userIds);
        return userRoles.stream()
                .map(user -> new UserStudyResponse(user.getId().toString(), user.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<String> setLastLoggedInTimeForUser(String userId) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.ok("User not found with ID: " + userId);
        }
        User user = optionalUser.get();
        user.setLastLoggedInTime(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        updateUserActivityAuditLogin(userId, user.getLastLoggedInTime(), UserActivity.LOGIN);
        return ResponseEntity.ok("Last logged in time updated successfully for user " + userId);
    }

    @Override
    public ResponseEntity<String> setLogoutTimeForUser(String userId) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.ok("User not found with ID: " + userId);
        }
        Date logoutTime = new Timestamp(System.currentTimeMillis());
        updateUserActivityAuditLogin(userId, logoutTime, UserActivity.LOGOUT);
        return ResponseEntity.ok("Log out updated successfully for user " + userId);
    }




    private String getUserRolesCommaSaperated(User user) {
        if(user != null && user.getRoles() != null) {
            return user.getRoles().stream().map(r -> r.getName()).collect(Collectors.joining(","));
        }
        return null;
    }
    private void updateUserActivityAuditLogin(String userId, Date value, UserActivity userActivity) {
        try {
            UserActivityAudit userActivityAudit = new UserActivityAudit();
            userActivityAudit.setUserId(userId);
            userActivityAudit.setActivity(userActivity.name());
            userActivityAudit.setActivityDate(value);
            userActivityAuditRepository.save(userActivityAudit);
        } catch (Exception e) {
            log.error("Filed to add user activity change log", e);
        }

    }

    private void updateUserProfileAudit(String userId, String newValue, String oldValue) {
        try {
            UserProfileAudit userProfileAudit = new UserProfileAudit();
            userProfileAudit.setUserId(userId);
            userProfileAudit.setAction(USER_PROFILE_AUDIT_ACTION_ROLE);
            userProfileAudit.setLevel(USER_PROFILE_AUDIT_LEVEL_SYSTEM);
            userProfileAudit.setNewValue(newValue);
            userProfileAudit.setOldValue(oldValue);
            userProfileAuditRepository.save(userProfileAudit);
        } catch (Exception e) {
            log.error("Filed to add user profile change log", e);
        }

    }
}

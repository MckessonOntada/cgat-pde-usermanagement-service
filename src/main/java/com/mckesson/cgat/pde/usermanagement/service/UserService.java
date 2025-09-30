package com.mckesson.cgat.pde.usermanagement.service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mckesson.cgat.pde.usermanagement.dto.ResendNumberVerificationDTO;
import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import com.mckesson.cgat.pde.usermanagement.exception.custom.FileNotValidException;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.model.Provider;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	User getUser(Long userId) throws ResourceNotFoundException;

	void deleteUser(Long userId) throws ResourceNotFoundException;

	void activateUser(Long userId) throws ResourceNotFoundException;

	void delinkUserFromAccount(Long userId, Long accountId) throws ResourceNotFoundException;

	Page<User> findAll(Pageable paging, String query, List<String> status, Long practiceId, LocalDate startDate,
			LocalDate endDate, List<Long> roleIds);

	User updateUser(Long userId, User model) throws ResourceNotFoundException;

	User createUser(User model);

	Boolean uploadUsers(MultipartFile userData, UserType userType, Long practiceId, OutputStream outputStream)
			throws FileNotValidException;

	User linkUserToAccounts(Long userId, List<Long> accountIds) throws ResourceNotFoundException;

	User linkUserToRoleAndPermissions(Long userId, UserType userType, RolePermissionsPatchDTO rolePermissions)
			throws ResourceNotFoundException;

	User getUserByEmail(String email) throws ResourceNotFoundException;

	User setDefaultPayToAccount(Long userId, String payToAccount) throws ResourceNotFoundException;

	User retryOktaProvisioning(Long userId) throws ResourceNotFoundException;

	void createPhysicianUser(Provider provider) throws ResourceNotFoundException;

	void updatePhysicianUser(Provider provider) throws ResourceNotFoundException;

	void linkPhysicianToAccounts(String email, List<Long> accountIds) throws ResourceNotFoundException;

	void deLinkPhysicianFromAccounts(String email, Long accountId) throws ResourceNotFoundException;

	void activatePhysicianUser(String email) throws ResourceNotFoundException;

	void resetPasswordByEmail(String email);

	void resendActivationEmail(String email) throws CGATHttpException, JsonProcessingException;

	void updateLoginTime(Long userId) throws ResourceNotFoundException;

	User updateCommunicationPreferences(Long userId, UserCommunicationPreferences communicationPreferences)
			throws ResourceNotFoundException;

	UserCommunicationPreferencesDTO fetchCommunicationPreferences(LoggedInUserDetails loggedInUser)
			throws ResourceNotFoundException;

	User updateUserNumberById(Long userId, String number) throws ResourceNotFoundException;

	OffsetDateTime resendNumberVerificationSms(Long userId) throws ResourceNotFoundException;

	ResendNumberVerificationDTO resendNumberVerificationDetails(Long userId) throws ResourceNotFoundException;

}

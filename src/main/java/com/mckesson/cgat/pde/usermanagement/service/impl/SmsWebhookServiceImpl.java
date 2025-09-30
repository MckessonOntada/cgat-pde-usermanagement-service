package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.SmsEventRequestDTO;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.SmsWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsWebhookServiceImpl implements SmsWebhookService {

	private final UserRepository userRepository;

	@Override
	public void processSmsEvent(SmsEventRequestDTO requestDto) {
		// Extract phone number and message
		String senderPhone = requestDto.getData().getFrom();
		String messageText = requestDto.getData().getMessage();

		List<User> users = userRepository.findAllByContact_Phone(senderPhone);

		if (users.isEmpty()) {
			log.warn("No user found for phone number: {}", senderPhone);
			return;
		}

		log.info("Found {} user(s) for phone: {}", users.size(), senderPhone);

		if ("yes".equalsIgnoreCase(messageText)) {
			users.forEach(user -> {
				if (Boolean.FALSE.equals(user.getIsPhoneVerified())) {
					user.setIsPhoneVerified(true);
					log.info("User {} marked as SMS confirmed", user.getId());
				}
				else {
					log.info("User {} already SMS confirmed", user.getId());
				}
			});
			userRepository.saveAll(users);
		}
		else {
			log.info("Ignored message '{}' from {}", messageText, senderPhone);
		}
	}

}
package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.SmsEventRequestDTO;

public interface SmsWebhookService {

	void processSmsEvent(SmsEventRequestDTO requestDto);

}
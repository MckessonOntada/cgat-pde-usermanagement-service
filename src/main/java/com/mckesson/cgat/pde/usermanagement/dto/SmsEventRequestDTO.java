package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsEventRequestDTO {

	private String id;

	private String topic;

	private String subject;

	private SmsEventData data;

	private String eventType;

	private String dataVersion;

	private String metadataVersion;

	private ZonedDateTime eventTime;

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SmsEventData {

	private String messageId;

	private String from;

	private String to;

	private String message;

	private int segmentCount;

	private ZonedDateTime receivedTimestamp;

}

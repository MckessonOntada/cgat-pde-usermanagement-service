package com.mckesson.cgat.pde.usermanagement.http;

import com.mckesson.cgat.pde.usermanagement.exception.custom.CGATHttpException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CGATHttpClient {

	private HttpClient httpClient = null;

	@PostConstruct
	public void init() {

		httpClient = newClient();
	}

	private HttpClient newClient() {
		return HttpClient.newBuilder().build();
	}

	public String sendPostRequest(String url, HashMap<String, String> queryParams, String payload, String oktaApiToken)
			throws CGATHttpException {
		String query = getQueryParams(queryParams);

		HttpRequest request = HttpRequest.newBuilder()
			.POST(HttpRequest.BodyPublishers.ofString(payload))
			.uri(URI.create(url + '?' + query))
			.header("Content-Type", "application/json")
			.header("Authorization", oktaApiToken)
			.timeout(Duration.ofSeconds(5))
			.build();
		log.debug("Sending POST request to URL: {} with Headers: {} and Payload: {}", request.uri(),
				request.headers().map(), payload);

		HttpResponse<String> httpResponse = getHttpResponse(request);
		log.debug("Received POST response for: {} with Status: {} and Body: {}", request.uri(),
				httpResponse.statusCode(), httpResponse.body());
		return httpResponse.body();

	}

	private String getQueryParams(HashMap<String, String> queryParams) {
		return queryParams.keySet()
			.stream()
			.map(key -> key + "=" + URLEncoder.encode(queryParams.get(key), StandardCharsets.UTF_8))
			.collect(Collectors.joining("&"));
	}

	public String sendGetRequest(String url, HashMap<String, String> queryParams, String oktaApiToken) {
		String query = getQueryParams(queryParams);
		HttpRequest request = HttpRequest.newBuilder()
			.GET()
			.uri(URI.create(url + '?' + query))
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, oktaApiToken)
			.timeout(Duration.ofSeconds(5))
			.build();

		log.debug("Sending GET request to URL: {} with Headers: {}", request.uri(), request.headers().map());

		HttpResponse<String> httpResponse = getHttpResponse(request);
		log.debug("Received GET response for: {} with Status: {} and Body: {}", request.uri(),
				httpResponse.statusCode(), httpResponse.body());

		return httpResponse.body();
	}

	public String sendPutRequest(String url, String oktaApiToken) {
		HttpRequest request = HttpRequest.newBuilder()
			.PUT(HttpRequest.BodyPublishers.ofString("Assigning user to group"))
			.uri(URI.create(url))
			.header(HttpHeaders.AUTHORIZATION, oktaApiToken)
			.timeout(Duration.ofSeconds(5))
			.build();
		log.debug("Sending PUT request to URL: {} with Headers: {} and Payload: {}", request.uri(),
				request.headers().map(), "Assigning user to group");

		HttpResponse<String> httpResponse = getHttpResponse(request);
		log.debug("Received PUT response for: {} with Status: {} and Body: {}", request.uri(),
				httpResponse.statusCode(), httpResponse.body());

		return httpResponse.body();
	}

	private HttpResponse<String> getHttpResponse(HttpRequest request) {
		try {
			HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			int statusCode = httpResponse.statusCode();
			boolean isValid = (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NO_CONTENT);

			if (!isValid) {
				throw new CGATHttpException("Communication Response - Received Response From report server with Status="
						+ httpResponse.body());
			}

			return httpResponse;
		}
		catch (Exception e) {
			log.error("error occurred ", e);
			throw new CGATHttpException(e);
		}
	}

}

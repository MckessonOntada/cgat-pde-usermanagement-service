package com.mckesson.cgat.pde.usermanagement.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Consumer;

@Slf4j
public class CGATWebClient {

	private final WebClient webClient;

	private CGATWebClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public static class CGATWebClientBuilder {

		private String baseURL;

		private Consumer<WebClient.Builder> customizer;

		private String clientSecret;

		private String clientId;

		private Duration responseTimeout = Duration.ofSeconds(10);

		private long maxAttempt = 3;

		private Duration backoff = Duration.ofSeconds(2);

		public CGATWebClientBuilder baseURL(String baseURL) {
			this.baseURL = baseURL;
			return this;
		}

		public CGATWebClientBuilder customizer(Consumer<WebClient.Builder> customizer) {
			this.customizer = customizer;
			return this;
		}

		public CGATWebClientBuilder withClientIdSecret(String clientId, String clientSecret) {
			this.clientId = clientId;
			this.clientSecret = clientSecret;
			return this;
		}

		public CGATWebClientBuilder responseTimeout(long responseTimeoutInSeconds) {
			this.responseTimeout = Duration.ofSeconds(responseTimeoutInSeconds);
			return this;
		}

		public CGATWebClientBuilder withBackoff(long maxAttempt, long backoffTimeInSeconds) {
			this.maxAttempt = maxAttempt;
			this.backoff = Duration.ofSeconds(backoffTimeInSeconds);
			return this;
		}

		public CGATWebClient build() {
			Assert.notNull(baseURL, "Base URL is required");
			WebClient.Builder builder = WebClient.builder();
			builder.baseUrl(baseURL).defaultHeader("Content-Type", "application/json").filter((request, next) -> {
				log.debug("Request: {} {}", request.method(), request);
				return next.exchange(request)
					.doOnNext(response -> log.trace("Response: {}", response.statusCode()))
					.retryWhen(Retry.backoff(maxAttempt, backoff)
						.doBeforeRetry(retrySignal -> log.warn("Retrying...", retrySignal.failure())));
			})
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create()
					.responseTimeout(responseTimeout)
					.resolver(DefaultAddressResolverGroup.INSTANCE)
					.secure(sslContextSpec -> sslContextSpec.sslContext(
							SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)))));
			if (clientId != null && clientSecret != null) {
				builder.defaultHeader("client_id", clientId).defaultHeader("client_secret", clientSecret);
			}
			else {
				log.warn("Client Id and Client Secret not provided");
			}

			if (customizer != null) {
				customizer.accept(builder);
			}
			return new CGATWebClient(builder.build());
		}

	}

	private static void handleError(WebClientResponseException e) {
		log.error("Error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
		throw e;
	}

	public <T> T get(String uri, Class<T> responseType) {
		return webClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(responseType)
			.doOnError(WebClientResponseException.class, CGATWebClient::handleError)
			.block();
	}

	public <T> T post(String uri, Object body, Class<T> responseType) {
		return webClient.post()
			.uri(uri)
			.bodyValue(body)
			.retrieve()
			.bodyToMono(responseType)
			.doOnError(WebClientResponseException.class, CGATWebClient::handleError)
			.block();
	}

	/*
	 * public static void main(String[] args) { CGATWebClient cgatWebClient = new
	 * CGATWebClientBuilder() .baseURL(
	 * "https://api-dev.nonprod.az.mckesson.com/pharma/pharma-pde-orders-papi-dev2/v1/")
	 * // .withClientIdSecret("0oaafa3zieEfUUnGf1d7",
	 * "XQU7QwwJIG0mgaOSQO8vnslTFxL8ircwylZSpPJVkID3o-yqilxsAuAMdBRsg_Mi") .build();
	 * System.out.println(cgatWebClient.get("/salesorders/123/456", String.class)); }
	 */

}

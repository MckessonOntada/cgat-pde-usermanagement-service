package com.mckesson.cgat.pde.usermanagement.config;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.ehcache.event.EventType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.time.Duration;

@Configuration
@EnableCaching
public class EhCacheConfig {

	@Bean
	public CacheManager ehCacheManager() {
		CacheManager cacheManager = Caching.getCachingProvider(EhcacheCachingProvider.class.getName())
			.getCacheManager();

		ResourcePoolsBuilder sharedPool = ResourcePoolsBuilder.heap(2000).offheap(300, MemoryUnit.MB);

		CacheEventListenerConfigurationBuilder eventListenerConfig = CacheEventListenerConfigurationBuilder
			.newEventListenerConfiguration(CustomCacheEventListener.class, EventType.CREATED, EventType.UPDATED,
					EventType.REMOVED)
			.unordered()
			.synchronous();

		CacheConfiguration<String, Page<Therapy>> allTherapyConfiguration = CacheConfigurationBuilder
			.newCacheConfigurationBuilder(String.class, (Class<Page<Therapy>>) (Class<?>) Page.class, sharedPool)
			.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(java.time.Duration.ofDays(1)))
			.withService(eventListenerConfig)
			.build();

		CacheConfiguration<String, MaterialDetailsResponseDTO> therapyConfiguration = CacheConfigurationBuilder
			.newCacheConfigurationBuilder(String.class, MaterialDetailsResponseDTO.class, sharedPool)
			.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(1)))
			.withService(eventListenerConfig)
			.build();

		CacheConfiguration<String, Object> defaultCacheConfig = CacheConfigurationBuilder
			.newCacheConfigurationBuilder(String.class, Object.class, sharedPool)
			.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(1)))
			.withService(eventListenerConfig)
			.build();

		javax.cache.configuration.Configuration<String, Page<Therapy>> allTherapyCacheConfig = Eh107Configuration
			.fromEhcacheCacheConfiguration(allTherapyConfiguration);

		javax.cache.configuration.Configuration<String, MaterialDetailsResponseDTO> therapyConfig = Eh107Configuration
			.fromEhcacheCacheConfiguration(therapyConfiguration);

		javax.cache.configuration.Configuration<String, Object> defaultCache = Eh107Configuration
			.fromEhcacheCacheConfiguration(defaultCacheConfig);

		cacheManager.createCache("materialCache", allTherapyCacheConfig);
		cacheManager.createCache("singleMaterialCache", therapyConfig);
		cacheManager.createCache("defaultCache", defaultCache);

		return cacheManager;
	}

}
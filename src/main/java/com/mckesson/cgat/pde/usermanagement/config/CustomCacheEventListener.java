package com.mckesson.cgat.pde.usermanagement.config;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@NoArgsConstructor
@Slf4j
public class CustomCacheEventListener implements CacheEventListener<Object, Object> {

	@Override
	public void onEvent(CacheEvent<? extends Object, ? extends Object> event) {
		log.info("🔥 Cache Event: {} | Key: {} | Old Value: {} | New Value: {}", event.getType(), event.getKey(),
				event.getOldValue(), event.getNewValue());
	}

}
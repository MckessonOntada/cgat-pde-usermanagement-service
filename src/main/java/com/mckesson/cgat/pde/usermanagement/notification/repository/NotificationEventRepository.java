package com.mckesson.cgat.pde.usermanagement.notification.repository;

import com.mckesson.cgat.pde.usermanagement.model.NotificationEvent;
import com.mckesson.cgat.pde.usermanagement.notification.config.NotificationEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {

	Optional<NotificationEvent> findTopByTriggerIdAndNotificationEventTypeOrderByCreatedAtDesc(Long userId,
			NotificationEventType type);

}

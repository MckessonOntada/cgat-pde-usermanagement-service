package com.mckesson.cgat.pde.usermanagement.notification.repository;

import com.mckesson.cgat.pde.usermanagement.notification.model.EmailNotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationEventRepository extends JpaRepository<EmailNotificationEvent, Long> {

}

package com.mckesson.cgat.pde.usermanagement.notification.repository;

import com.mckesson.cgat.pde.usermanagement.model.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {

}

package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
}

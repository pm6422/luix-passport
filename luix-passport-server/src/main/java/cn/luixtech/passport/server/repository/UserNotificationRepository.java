package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link UserNotification} entity.
 */
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, String> {
}

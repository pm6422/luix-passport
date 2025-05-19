package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserNotification} entity.
 */
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, String> {
    long countByUserIdAndStatus(String userId, String status);

    List<UserNotification> findByUserIdAndActiveIsTrueOrderByCreatedAtDesc(String userId);
}

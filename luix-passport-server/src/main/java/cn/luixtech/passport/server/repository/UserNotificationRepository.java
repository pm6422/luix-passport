package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link UserNotification} entity.
 */
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, String> {
    long countByUserIdAndStatus(String userId, String status);

    @Query(value = "SELECT un.* FROM user_notification un " +
            "JOIN notification n ON n.id = un.notification_id " +
            "WHERE un.user_id = :userId " +
            "AND un.active = true " +
            "AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) " +
            "ORDER BY n.created_at DESC",
            countQuery = "SELECT count(un.id) FROM user_notification un " +
                    "JOIN notification n ON n.id = un.notification_id " +
                    "WHERE un.user_id = :userId " +
                    "AND un.active = true " +
                    "AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)",
            nativeQuery = true)
    Page<UserNotification> findByUserAndKeyword(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);
}

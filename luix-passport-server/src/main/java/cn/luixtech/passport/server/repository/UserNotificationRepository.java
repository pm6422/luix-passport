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
    long countByReceiverIdAndStatus(String userId, String status);

    Page<UserNotification> findByReceiverId(Pageable pageable, String userId);

    @Query("SELECT un FROM UserNotification un " +
            "JOIN FETCH un.notification n " +  // 使用FETCH避免N+1问题
            "WHERE un.receiverId = :userId " +
            "AND un.active = true " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "     (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)) " +
            "ORDER BY n.createdAt DESC")
    Page<UserNotification> findByUserAndKeyword(
            Pageable pageable,
            @Param("userId") String userId,
            @Param("keyword") String keyword);
}

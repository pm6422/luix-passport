package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, String> {
    long countByReceiverIdAndStatus(String username, String status);

    Page<UserNotification> findByReceiverId(Pageable pageable, String username);

    @Query("SELECT un FROM UserNotification un " +
            "JOIN FETCH un.notification n " +  // 使用FETCH避免N+1问题
            "WHERE un.receiverId = :receiverId " +
            "AND un.active = true " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "     (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)) " +
            "ORDER BY n.createdAt DESC")
    Page<UserNotification> findByReceiverAndKeyword(
            Pageable pageable,
            @Param("receiverId") String receiverId,
            @Param("keyword") String keyword);
}

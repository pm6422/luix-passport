package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.Inbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, String> {
    long countByReceiverIdAndStatus(String username, String status);

    Page<Inbox> findByReceiverId(Pageable pageable, String username);

    @Query("SELECT un FROM Inbox un " +
            "JOIN FETCH un.notification n " +  // 使用FETCH避免N+1问题
            "WHERE un.receiverId = :receiverId " +
            "AND un.active = true " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "     (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)) " +
            "ORDER BY n.createdAt DESC")
    Page<Inbox> findByReceiverAndKeyword(
            Pageable pageable,
            @Param("receiverId") String receiverId,
            @Param("keyword") String keyword);
}

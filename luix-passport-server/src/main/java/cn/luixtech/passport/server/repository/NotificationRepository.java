package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Notification} entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
}

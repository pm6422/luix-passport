package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserAuthEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

/**
 * Spring Data JPA repository for the {@link UserAuthEvent} entity.
 */
@Repository
public interface UserAuthEventRepository extends JpaRepository<UserAuthEvent, String> {

    Long countByEventAndCreatedAtBetween(String authSuccess, Instant yesterday, Instant nextDay);
}

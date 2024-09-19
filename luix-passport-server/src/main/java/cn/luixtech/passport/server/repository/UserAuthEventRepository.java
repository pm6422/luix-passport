package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserAuthEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link UserAuthEvent} entity.
 */
@Repository
public interface UserAuthEventRepository extends JpaRepository<UserAuthEvent, String> {

}

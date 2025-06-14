package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.Shedlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Shedlock} entity.
 */
@Repository
public interface ShedlockRepository extends JpaRepository<Shedlock, String> {

}

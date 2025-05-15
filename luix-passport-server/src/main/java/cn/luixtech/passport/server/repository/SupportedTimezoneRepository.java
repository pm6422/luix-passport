package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.SupportedTimezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link SupportedTimezone} entity.
 */
@Repository
public interface SupportedTimezoneRepository extends JpaRepository<SupportedTimezone, String> {

    Optional<SupportedTimezone> findByPresetIsTrue();
}

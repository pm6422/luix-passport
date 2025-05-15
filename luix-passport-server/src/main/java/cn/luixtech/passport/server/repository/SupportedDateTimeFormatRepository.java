package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.SupportedDateTimeFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link SupportedDateTimeFormat} entity.
 */
@Repository
public interface SupportedDateTimeFormatRepository extends JpaRepository<SupportedDateTimeFormat, String> {

    Optional<SupportedDateTimeFormat> findByPresetIsTrue();
}

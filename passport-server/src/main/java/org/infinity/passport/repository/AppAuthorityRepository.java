package org.infinity.passport.repository;

import org.infinity.passport.domain.AppAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link AppAuthority} entity.
 */
@Repository
public interface AppAuthorityRepository extends JpaRepository<AppAuthority, String> {

    List<AppAuthority> findByAppId(String appId);

    Optional<AppAuthority> findOneByAppIdAndAuthorityName(String appId, String authorityName);

    void deleteByAppId(String appId);

}

package org.infinity.passport.repository;

import java.util.List;
import java.util.Optional;

import org.infinity.passport.domain.AppAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the AppAuthority entity.
 */
public interface AppAuthorityRepository extends MongoRepository<AppAuthority, String> {

    Page<AppAuthority> findByAuthorityName(Pageable pageable, String authorityName);

    List<AppAuthority> findByAppName(String appName);

    Page<AppAuthority> findByAppName(Pageable pageable, String appName);

    Page<AppAuthority> findByAppNameAndAuthorityName(Pageable pageable, String appName, String authorityName);

    Optional<AppAuthority> findOneByAppNameAndAuthorityName(String appName, String authorityName);

    void deleteByAppName(String name);

}

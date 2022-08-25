package com.luixtech.passport.repository;

import com.luixtech.passport.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    int countByName(String name);

    List<Authority> findByEnabled(Boolean enabled);

}

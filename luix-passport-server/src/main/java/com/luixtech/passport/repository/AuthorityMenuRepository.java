package com.luixtech.passport.repository;

import com.luixtech.passport.domain.AuthorityMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link AuthorityMenu} entity.
 */
@Repository
public interface AuthorityMenuRepository extends JpaRepository<AuthorityMenu, String> {

    List<AuthorityMenu> findByAuthorityNameIn(List<String> authorityNames);

    void deleteByAuthorityName(String authorityName);
}

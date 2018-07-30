package org.infinity.passport.repository;

import java.util.List;

import org.infinity.passport.domain.AuthorityAdminMenu;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the AuthorityAdminMenu entity.
 */
public interface AuthorityAdminMenuRepository extends MongoRepository<AuthorityAdminMenu, String> {

    List<AuthorityAdminMenu> findByAuthorityNameIn(List<String> authorityNames);

    List<AuthorityAdminMenu> findByAuthorityName(String authorityName);

    void deleteByAuthorityNameAndAdminMenuIdIn(String authorityName, List<String> adminMenuIds);
}

package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Org} entity.
 */
@Repository
public interface OrgRepository extends JpaRepository<Org, String> {
    long countByEnabledIsTrue();
}

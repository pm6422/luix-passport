package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.OrgUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link OrgUser} entity.
 */
@Repository
public interface OrgUserRepository extends JpaRepository<OrgUser, String> {

    List<OrgUser> findByOrgId(String teamId);

    void deleteByOrgId(String teamId);
}

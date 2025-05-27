package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.OrgUser;
import cn.luixtech.passport.server.repository.OrgUserRepository;
import cn.luixtech.passport.server.service.OrgUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static cn.luixtech.passport.server.persistence.Tables.ORG_USER;

@Service
@AllArgsConstructor
public class OrgUserServiceImpl implements OrgUserService {

    private       OrgUserRepository orgUserRepository;
    private final DSLContext        dslContext;

    @Override
    public Set<String> findOrgIdsByUsername(String username) {
        return dslContext.select(ORG_USER.ORG_ID)
                .from(ORG_USER)
                .where(ORG_USER.USERNAME.eq(username))
                .fetchSet(ORG_USER.ORG_ID);
    }

    @Override
    public void save(String orgId, Set<String> usernames) {
        List<OrgUser> orgUsers = orgUserRepository.findByOrgId(orgId);
        if (CollectionUtils.isEmpty(orgUsers)) {
            // insert
            usernames.forEach(username -> {
                OrgUser orgUser = new OrgUser(orgId, username);
                orgUserRepository.save(orgUser);
            });
        } else {
            // delete
            orgUserRepository.deleteByOrgId(orgId);
            // insert
            usernames.forEach(username -> {
                OrgUser orgUser = new OrgUser(orgId, username);
                orgUserRepository.save(orgUser);
            });
        }
    }

    @Override
    public void save(String orgId, String username) {
        save(orgId, Set.of(username));
    }
}

package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.OrgUser;
import cn.luixtech.passport.server.repository.OrgUserRepository;
import cn.luixtech.passport.server.service.OrgUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrgUserServiceImpl implements OrgUserService {
    private OrgUserRepository orgUserRepository;

    @Override
    public void save(String orgId, Set<String> userIds) {
        List<OrgUser> orgUsers = orgUserRepository.findByOrgId(orgId);
        if (CollectionUtils.isEmpty(orgUsers)) {
            // insert
            userIds.forEach(userId -> {
                OrgUser orgUser = new OrgUser(orgId, userId);
                orgUserRepository.save(orgUser);
            });
        } else {
            // delete
            orgUserRepository.deleteByOrgId(orgId);
            // insert
            userIds.forEach(userId -> {
                OrgUser orgUser = new OrgUser(orgId, userId);
                orgUserRepository.save(orgUser);
            });
        }
    }

    @Override
    public void save(String orgId, String userId) {
        save(orgId, Set.of(userId));
    }
}

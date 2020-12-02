package org.infinity.passport.config.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.domain.MongoOAuth2Approval;
import org.infinity.passport.repository.OAuth2ApprovalRepository;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MongoApprovalStore implements ApprovalStore {

    private final OAuth2ApprovalRepository oAuth2ApprovalRepository;

    private boolean handleRevocationsAsExpiry = false;

    public MongoApprovalStore(OAuth2ApprovalRepository oAuth2ApprovalRepository) {
        this.oAuth2ApprovalRepository = oAuth2ApprovalRepository;
    }

    public void setHandleRevocationsAsExpiry(boolean handleRevocationsAsExpiry) {
        this.handleRevocationsAsExpiry = handleRevocationsAsExpiry;
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        log.debug("Adding approvals: {}", approvals);

        for (final Approval approval : approvals) {
            List<MongoOAuth2Approval> mongoDBApprovals = this.oAuth2ApprovalRepository
                    .findByUserIdAndClientIdAndScope(approval.getUserId(), approval.getClientId(), approval.getScope());

            if (!mongoDBApprovals.isEmpty()) {
                for (final MongoOAuth2Approval mongoDBApproval : mongoDBApprovals) {
                    updateApproval(mongoDBApproval, approval);
                }
            } else {
                updateApproval(new MongoOAuth2Approval(), approval);
            }
        }
        return true;
    }

    private void updateApproval(final MongoOAuth2Approval mongoDBApproval, final Approval approval) {
        log.debug("Refreshing approval: {}", approval);

        mongoDBApproval.setExpiresAt(approval.getExpiresAt());
        mongoDBApproval.setStatus(approval.getStatus() == null ? Approval.ApprovalStatus.APPROVED : approval.getStatus());
        mongoDBApproval.setLastUpdatedAt(approval.getLastUpdatedAt());
        mongoDBApproval.setUserId(approval.getUserId());
        mongoDBApproval.setClientId(approval.getClientId());
        mongoDBApproval.setScope(approval.getScope());

        this.oAuth2ApprovalRepository.save(mongoDBApproval);
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        log.debug("Revoking approvals: {}", approvals);
        boolean success = true;

        for (final Approval approval : approvals) {
            List<MongoOAuth2Approval> mongoDBApprovals = this.oAuth2ApprovalRepository
                    .findByUserIdAndClientIdAndScope(approval.getUserId(), approval.getClientId(), approval.getScope());

            if (mongoDBApprovals.size() != 1) {
                success = false;
            }

            for (final MongoOAuth2Approval mongoDBApproval : mongoDBApprovals) {
                if (handleRevocationsAsExpiry) {
                    mongoDBApproval.setExpiresAt(new Date());
                    this.oAuth2ApprovalRepository.save(mongoDBApproval);
                } else {
                    this.oAuth2ApprovalRepository.delete(mongoDBApproval);
                }
            }
        }
        return success;
    }

    @Override
    public Collection<Approval> getApprovals(String userId, String clientId) {
        return new ArrayList<>(this.oAuth2ApprovalRepository.findByUserIdAndClientId(userId, clientId));
    }
}

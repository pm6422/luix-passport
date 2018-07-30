package org.infinity.passport.config.oauth2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinity.passport.domain.MongoOAuth2Approval;
import org.infinity.passport.repository.OAuth2ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Component;

@Component
public class MongoApprovalStore implements ApprovalStore {

    private final Log                logger                    = LogFactory.getLog(getClass());

    @Autowired
    private OAuth2ApprovalRepository oAuth2ApprovalRepository;

    private boolean                  handleRevocationsAsExpiry = false;

    public void setHandleRevocationsAsExpiry(boolean handleRevocationsAsExpiry) {
        this.handleRevocationsAsExpiry = handleRevocationsAsExpiry;
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        logger.debug(String.format("adding approvals: [%s]", approvals));

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
        logger.debug(String.format("refreshing approval: [%s]", approval));

        mongoDBApproval.setExpiresAt(approval.getExpiresAt());
        mongoDBApproval
                .setStatus(approval.getStatus() == null ? Approval.ApprovalStatus.APPROVED : approval.getStatus());
        mongoDBApproval.setLastUpdatedAt(approval.getLastUpdatedAt());
        mongoDBApproval.setUserId(approval.getUserId());
        mongoDBApproval.setClientId(approval.getClientId());
        mongoDBApproval.setScope(approval.getScope());

        this.oAuth2ApprovalRepository.save(mongoDBApproval);
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        logger.debug(String.format("Revoking approvals: [%s]", approvals));
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

package org.infinity.passport.repository;

import java.util.List;

import org.infinity.passport.domain.MongoOAuth2Approval;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the MongoOAuth2Approval entity.
 */
public interface OAuth2ApprovalRepository extends MongoRepository<MongoOAuth2Approval, String> {

    List<MongoOAuth2Approval> findByUserIdAndClientId(String userId, String clientId);

    List<MongoOAuth2Approval> findByUserIdAndClientIdAndScope(String userId, String clientId, String scope);
}

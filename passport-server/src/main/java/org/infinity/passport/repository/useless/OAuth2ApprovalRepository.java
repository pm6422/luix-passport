package org.infinity.passport.repository.useless;

import org.infinity.passport.domain.useless.MongoOAuth2Approval;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the MongoOAuth2Approval entity.
 */
@Repository
public interface OAuth2ApprovalRepository extends MongoRepository<MongoOAuth2Approval, String> {

    List<MongoOAuth2Approval> findByUserIdAndClientId(String userId, String clientId);

    List<MongoOAuth2Approval> findByUserIdAndClientIdAndScope(String userId, String clientId, String scope);
}

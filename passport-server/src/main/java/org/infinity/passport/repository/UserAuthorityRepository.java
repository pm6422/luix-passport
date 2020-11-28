package org.infinity.passport.repository;

import java.util.List;
import java.util.Optional;

import org.infinity.passport.domain.UserAuthority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the UserAuthority entity.
 */
@Repository
public interface UserAuthorityRepository extends MongoRepository<UserAuthority, String> {

    List<UserAuthority> findByUserId(String userId);

    void deleteByUserId(String userId);

}

package org.infinity.passport.repository;

import org.infinity.passport.domain.UserProfilePhoto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the UserProfilePhoto entity.
 */
@Repository
public interface UserProfilePhotoRepository extends MongoRepository<UserProfilePhoto, String> {

    UserProfilePhoto findByUserName(String userName);
}

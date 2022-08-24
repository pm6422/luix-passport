package com.luixtech.passport.repository;

import com.luixtech.passport.domain.UserProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link UserProfilePhoto} entity.
 */
@Repository
public interface UserProfilePhotoRepository extends JpaRepository<UserProfilePhoto, String> {

    Optional<UserProfilePhoto> findByUserId(String userId);
}

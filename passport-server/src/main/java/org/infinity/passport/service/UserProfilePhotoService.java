package org.infinity.passport.service;

import org.infinity.passport.domain.UserProfilePhoto;

public interface UserProfilePhotoService {

    UserProfilePhoto insert(String userId, byte[] photoData);

    void update(String id, byte[] photoData);
}
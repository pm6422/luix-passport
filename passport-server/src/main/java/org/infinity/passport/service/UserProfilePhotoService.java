package org.infinity.passport.service;

import org.infinity.passport.domain.UserProfilePhoto;

public interface UserProfilePhotoService {

    UserProfilePhoto insert(String userName, byte[] photoData);

    void update(String id, String userName, byte[] photoData);
}
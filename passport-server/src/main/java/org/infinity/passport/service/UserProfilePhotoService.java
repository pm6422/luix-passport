package org.infinity.passport.service;

import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserProfilePhoto;

public interface UserProfilePhotoService {

    void insert(String userId, byte[] photoData);

    void update(UserProfilePhoto photo, byte[] photoData);

    void save(User user, byte[] photoData);
}
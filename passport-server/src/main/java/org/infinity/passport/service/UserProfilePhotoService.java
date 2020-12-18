package org.infinity.passport.service;

import org.infinity.passport.domain.User;

public interface UserProfilePhotoService {

    void insert(String userId, byte[] photoData);

    void update(String id, byte[] photoData);

    void save(User user, byte[] photoData);
}
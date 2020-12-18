package org.infinity.passport.service;

public interface UserProfilePhotoService {

    void insert(String userId, byte[] photoData);

    void update(String id, byte[] photoData);
}
package com.luixtech.passport.service;

import com.luixtech.passport.domain.User;
import com.luixtech.passport.domain.UserProfilePhoto;

public interface UserProfilePhotoService {

    void insert(String userId, byte[] photoData);

    void update(UserProfilePhoto photo, byte[] photoData);

    void save(User user, byte[] photoData);
}
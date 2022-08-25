package com.luixtech.passport.service.impl;

import lombok.AllArgsConstructor;
import com.luixtech.passport.domain.User;
import com.luixtech.passport.domain.UserProfilePhoto;
import com.luixtech.passport.repository.UserProfilePhotoRepository;
import com.luixtech.passport.repository.UserRepository;
import com.luixtech.passport.service.UserProfilePhotoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserProfilePhotoServiceImpl implements UserProfilePhotoService {
    private final UserProfilePhotoRepository userProfilePhotoRepository;
    private final UserRepository             userRepository;

    @Override
    public void insert(String userId, byte[] photoData) {
        UserProfilePhoto photo = new UserProfilePhoto(userId, photoData);
        userProfilePhotoRepository.save(photo);
    }

    @Override
    public void update(UserProfilePhoto photo, byte[] photoData) {
        photo.setProfilePhoto(photoData);
        userProfilePhotoRepository.save(photo);
    }

    @Override
    public void save(User user, byte[] photoData) {
        Optional<UserProfilePhoto> existingPhoto = userProfilePhotoRepository.findByUserId(user.getId());
        if (existingPhoto.isPresent()) {
            // Update if exists
            update(existingPhoto.get(), photoData);
        } else {
            // Insert if not exists
            insert(user.getId(), photoData);
            user.setProfilePhotoEnabled(true);
            userRepository.save(user);
        }
    }
}

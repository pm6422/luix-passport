package org.infinity.passport.service.impl;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.exception.NoDataFoundException;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.UserProfilePhotoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfilePhotoServiceImpl implements UserProfilePhotoService {

    private final UserProfilePhotoRepository userProfilePhotoRepository;
    private final UserRepository             userRepository;

    public UserProfilePhotoServiceImpl(UserProfilePhotoRepository userProfilePhotoRepository,
                                       UserRepository userRepository) {
        this.userProfilePhotoRepository = userProfilePhotoRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void insert(String userId, byte[] photoData) {
        UserProfilePhoto photo = new UserProfilePhoto(userId, new Binary(BsonBinarySubType.BINARY, photoData));
        userProfilePhotoRepository.insert(photo);
    }

    @Override
    public void update(String id, byte[] photoData) {
        UserProfilePhoto existingPhoto = userProfilePhotoRepository.findById(id).orElseThrow(() -> new NoDataFoundException(id));
        existingPhoto.setProfilePhoto(new Binary(BsonBinarySubType.BINARY, photoData));
        userProfilePhotoRepository.save(existingPhoto);
    }

    @Override
    public void save(User user, byte[] photoData) {
        Optional<UserProfilePhoto> existingPhoto = userProfilePhotoRepository.findByUserId(user.getId());
        if (existingPhoto.isPresent()) {
            // Update if exists
            update(existingPhoto.get().getId(), photoData);
        } else {
            // Insert if not exists
            insert(user.getId(), photoData);
            // Update hasProfilePhoto to true
            user.setHasProfilePhoto(true);
            userRepository.save(user);
        }
    }
}

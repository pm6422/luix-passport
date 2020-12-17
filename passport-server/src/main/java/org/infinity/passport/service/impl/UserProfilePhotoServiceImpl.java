package org.infinity.passport.service.impl;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.exception.NoDataFoundException;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.service.UserProfilePhotoService;
import org.springframework.stereotype.Service;

@Service
public class UserProfilePhotoServiceImpl implements UserProfilePhotoService {

    private final UserProfilePhotoRepository userProfilePhotoRepository;

    public UserProfilePhotoServiceImpl(UserProfilePhotoRepository userProfilePhotoRepository) {
        this.userProfilePhotoRepository = userProfilePhotoRepository;
    }

    @Override
    public UserProfilePhoto insert(String userName, byte[] photoData) {
        UserProfilePhoto photo = new UserProfilePhoto(userName);
        photo.setProfilePhoto(new Binary(BsonBinarySubType.BINARY, photoData));
        photo = userProfilePhotoRepository.insert(photo);
        return photo;
    }

    @Override
    public void update(String id, String userName, byte[] photoData) {
        UserProfilePhoto existingPhoto = userProfilePhotoRepository.findById(id).orElseThrow(() -> new NoDataFoundException(id));
        existingPhoto.setUserName(userName);
        existingPhoto.setProfilePhoto(new Binary(BsonBinarySubType.BINARY, photoData));
        userProfilePhotoRepository.save(existingPhoto);
    }
}

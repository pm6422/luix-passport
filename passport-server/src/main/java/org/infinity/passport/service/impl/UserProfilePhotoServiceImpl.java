package org.infinity.passport.service.impl;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.service.UserProfilePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserProfilePhotoServiceImpl implements UserProfilePhotoService {

    @Autowired
    private UserProfilePhotoRepository userProfilePhotoRepository;

    @Override
    public UserProfilePhoto insert(String userName, MultipartFile file) throws IOException {
        UserProfilePhoto photo = new UserProfilePhoto(userName);
        photo.setProfilePhoto(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        photo = userProfilePhotoRepository.insert(photo);
        return photo;
    }

    @Override
    public void update(String id, String userName, MultipartFile file) throws IOException {
        UserProfilePhoto existingPhoto = userProfilePhotoRepository.findById(id).get();
        if (existingPhoto == null) {
            return;
        }
        existingPhoto.setUserName(userName);
        existingPhoto.setProfilePhoto(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        userProfilePhotoRepository.save(existingPhoto);
    }
}

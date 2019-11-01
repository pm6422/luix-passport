package org.infinity.passport.service;

import org.infinity.passport.domain.UserProfilePhoto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserProfilePhotoService {

    UserProfilePhoto insert(String userName, MultipartFile file) throws IOException;

    void update(String id, String userName, MultipartFile file) throws IOException;
}
package cn.luixtech.passport.server.service.impl;


import cn.luixtech.passport.server.domain.UserProfilePic;
import cn.luixtech.passport.server.repository.UserProfilePicRepository;
import cn.luixtech.passport.server.service.UserProfilePicService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Optional;

import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;

@Service
@AllArgsConstructor
public class UserProfilePicServiceImpl implements UserProfilePicService {
    private static final String                   DEFAULT_USER_PHOTO_URL = "/assets/images/cartoon/01.png";
    private final        UserProfilePicRepository userProfilePicRepository;

    @Override
    public byte[] getProfilePic(String username, HttpServletRequest request) throws IOException {
        Optional<UserProfilePic> userPhoto = userProfilePicRepository.findById(username);
        if (userPhoto.isPresent()) {
            return userPhoto.get().getProfilePic();
        }
        // Set the default profile picture
        return StreamUtils.copyToByteArray(
                new UrlResource(getRequestUrl(request) + DEFAULT_USER_PHOTO_URL).getInputStream());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(String username, byte[] photoData) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setId(username);
        userProfilePic.setProfilePic(photoData);
        userProfilePicRepository.save(userProfilePic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserProfilePic photo, byte[] photoData) {
        photo.setProfilePic(photoData);
        userProfilePicRepository.save(photo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, byte[] photoData) {
        Optional<UserProfilePic> existingOne = userProfilePicRepository.findById(username);
        if (existingOne.isPresent()) {
            // update if exists
            update(existingOne.get(), photoData);
        } else {
            // insert if not exists
            insert(username, photoData);
        }
    }
}

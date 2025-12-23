package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.UserAvatar;
import cn.luixtech.passport.server.repository.UserAvatarRepository;
import cn.luixtech.passport.server.service.UserAvatarService;
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
public class UserAvatarServiceImpl implements UserAvatarService {
    private static final String               DEFAULT_USER_PHOTO_URL = "/assets/images/cartoon/01.png";
    private final       UserAvatarRepository userAvatarRepository;

    @Override
    public byte[] getAvatar(String username, HttpServletRequest request) throws IOException {
        Optional<UserAvatar> userPhoto = userAvatarRepository.findById(username);
        if (userPhoto.isPresent()) {
            return userPhoto.get().getAvatar();
        }
        // Set the default avatar
        return StreamUtils.copyToByteArray(
                new UrlResource(getRequestUrl(request) + DEFAULT_USER_PHOTO_URL).getInputStream());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(String username, byte[] photoData) {
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setId(username);
        userAvatar.setAvatar(photoData);
        userAvatarRepository.save(userAvatar);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserAvatar photo, byte[] photoData) {
        photo.setAvatar(photoData);
        userAvatarRepository.save(photo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, byte[] photoData) {
        Optional<UserAvatar> existingOne = userAvatarRepository.findById(username);
        if (existingOne.isPresent()) {
            // update if exists
            update(existingOne.get(), photoData);
        } else {
            // insert if not exists
            insert(username, photoData);
        }
    }
}

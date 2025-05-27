package cn.luixtech.passport.server.service.impl;


import cn.luixtech.passport.server.domain.UserProfilePic;
import cn.luixtech.passport.server.repository.UserProfilePicRepository;
import cn.luixtech.passport.server.service.UserProfilePicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserProfilePicServiceImpl implements UserProfilePicService {
    private final UserProfilePicRepository userProfilePicRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insert(String username, byte[] photoData) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setId(username);
        userProfilePic.setProfilePic(photoData);
        userProfilePicRepository.save(userProfilePic);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void update(UserProfilePic photo, byte[] photoData) {
        photo.setProfilePic(photoData);
        userProfilePicRepository.save(photo);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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

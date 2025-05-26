package cn.luixtech.passport.server.service;


import cn.luixtech.passport.server.domain.UserProfilePic;

public interface UserProfilePicService {

    void insert(String userId, byte[] photoData);

    void update(UserProfilePic photo, byte[] photoData);

    void save(String userId, byte[] photoData);
}
package cn.luixtech.passport.server.service;


import cn.luixtech.passport.server.domain.UserProfilePic;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface UserProfilePicService {

    byte[] getProfilePic(String username, HttpServletRequest request) throws IOException;

    void insert(String username, byte[] photoData);

    void update(UserProfilePic photo, byte[] photoData);

    void save(String username, byte[] photoData);
}
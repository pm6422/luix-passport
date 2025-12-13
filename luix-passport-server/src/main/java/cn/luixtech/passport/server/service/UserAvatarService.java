package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.UserAvatar;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface UserAvatarService {

    String buildAvatarUrl(String username, HttpServletRequest request);

    byte[] getAvatarByUserToken(String userToken, HttpServletRequest request) throws IOException;

    byte[] getAvatar(String username, HttpServletRequest request) throws IOException;

    void insert(String username, byte[] photoData);

    void update(UserAvatar photo, byte[] photoData);

    void save(String username, byte[] photoData);
}
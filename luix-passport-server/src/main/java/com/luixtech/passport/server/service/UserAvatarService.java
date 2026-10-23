package com.luixtech.passport.server.service;

import com.luixtech.passport.server.domain.UserAvatar;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface UserAvatarService {

    byte[] getAvatar(String username, HttpServletRequest request) throws IOException;

    void insert(String username, byte[] photoData);

    void update(UserAvatar photo, byte[] photoData);

    void save(String username, byte[] photoData);
}
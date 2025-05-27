package cn.luixtech.passport.server.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;

/**
 * A server-sent event is when a web page automatically gets updates from a server.
 * Refer to <a href="https://juejin.cn/post/7122014462181113887">WEB实时消息推送7种方案</a>
 */
public interface SseService {

    SseEmitter add(String username);

    void remove(String username);

    void removeAll();

    void pushMessage(String username, String message);

    void cleanUp();

    Set<String> getOnlineUsernames();
}

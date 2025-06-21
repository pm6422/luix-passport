package cn.luixtech.passport.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SseMessage {
    public static final String OPERATION_PUSH   = "push";
    public static final String OPERATION_REMOVE = "remove";
    private             String operation;
    private             String username;
    private             String message;

    public static SseMessage buildPush(String username, String message) {
        return new SseMessage(OPERATION_PUSH, username, message);
    }

    public static SseMessage buildRemove(String username) {
        return new SseMessage(OPERATION_REMOVE, username, null);
    }
}

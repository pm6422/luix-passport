package cn.luixtech.passport.server.exception;

public class JobQueueException extends RuntimeException {
    public JobQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
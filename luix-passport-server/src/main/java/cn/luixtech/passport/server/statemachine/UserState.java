package cn.luixtech.passport.server.statemachine;

public enum UserState {
    CREATED,
    INACTIVATED,
    ACTIVATED,
    DISABLED,
    ENABLED,
    SIGNED_IN,
    SIGNED_OUT
}
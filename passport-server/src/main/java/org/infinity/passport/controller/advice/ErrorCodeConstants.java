package org.infinity.passport.controller.advice;

public final class ErrorCodeConstants {

    public static final String ERROR_CONCURRENCY_EXCEPTION     = "error.concurrency.exception";

    public static final String ERROR_SYSTEM_EXCEPTION          = "error.system.exception";

    public static final String ERROR_LOGIN_USER_NOT_EXIST      = "error.login.user.not.exist";

    public static final String ERROR_NO_AUTHORITIES            = "error.no.authorities";

    public static final String ERROR_DATA_NOT_EXIST            = "error.data.not.exist";

    public static final String WARN_ACCESS_DENIED              = "warn.access.denied";

    public static final String WARN_FIELDS_VALIDATION_ERROR    = "warn.fields.validation.error";

    public static final String WARN_METHOD_NOT_SUPPORTED       = "warn.method.not.supported";

    public static final String WARN_CUSTOM_PARAMETERIZED_ERROR = "warn.custom.parameterized.error";

    private ErrorCodeConstants() {
    }

}

package org.infinity.passport.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.dto.ParameterizedErrorDTO;
import org.infinity.passport.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
@Slf4j
public class ExceptionTranslatorAdvice {
    private final MessageSource messageSource;

    private final HttpHeaderCreator httpHeaderCreator;

    public ExceptionTranslatorAdvice(MessageSource messageSource, HttpHeaderCreator httpHeaderCreator) {
        this.messageSource = messageSource;
        this.httpHeaderCreator = httpHeaderCreator;
    }

    /**
     * JSR 303 Bean Validation Warn handler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> processBeanValidationException(MethodArgumentNotValidException ex) {
        String warnMessage = messageSource.getMessage(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR, null,
                ApplicationConstants.SYSTEM_LOCALE);
        // warn级别记录用户输入错误，error级别只记录系统逻辑出错、异常、或者重要的错误信息
        log.warn(warnMessage);
        return ResponseEntity.badRequest()
                .headers(httpHeaderCreator.createWarnHeader(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR))
                .body(processFieldErrors(ex.getBindingResult().getFieldErrors()));
    }

    /**
     * Field Validation Warn handler
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> processFieldValidationException(BindException ex) {
        String warnMessage = messageSource.getMessage(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR, null,
                ApplicationConstants.SYSTEM_LOCALE);
        // warn级别记录用户输入错误，error级别只记录系统逻辑出错、异常、或者重要的错误信息
        log.warn(warnMessage);
        return ResponseEntity.badRequest()
                .headers(httpHeaderCreator.createWarnHeader(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR))
                .body(processFieldErrors(ex.getBindingResult().getFieldErrors()));
    }

    /**
     * Field Validation Warn handler
     */
    @ExceptionHandler(FieldValidationException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> processFieldValidationException(FieldValidationException ex) {
        String warnMessage = messageSource.getMessage(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR, null,
                ApplicationConstants.SYSTEM_LOCALE);
        // warn级别记录用户输入错误，error级别只记录系统逻辑出错、异常、或者重要的错误信息
        log.warn(warnMessage);
        return ResponseEntity.badRequest()
                .headers(httpHeaderCreator.createWarnHeader(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR))
                .body(processFieldErrors(ex.getFieldErrors()));
    }

    /**
     * Login user not exist error handler
     */
    @ExceptionHandler(LoginUserNotExistException.class)
    @ResponseBody
    public ResponseEntity<ParameterizedErrorDTO> processLoginUserNotExistException(LoginUserNotExistException ex) {
        String errorMessage = messageSource.getMessage(ErrorCodeConstants.ERROR_LOGIN_USER_NOT_EXIST,
                new Object[]{ex.getUserName()}, ApplicationConstants.SYSTEM_LOCALE);
        ex.setMessage(errorMessage);
        log.error(errorMessage);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR).headers(httpHeaderCreator
                        .createErrorHeader(ErrorCodeConstants.ERROR_LOGIN_USER_NOT_EXIST, ex.getUserName()))
                .body(ex.getErrorDTO());
    }

    /**
     * No authority error handler
     */
    @ExceptionHandler(NoAuthorityException.class)
    @ResponseBody
    public ResponseEntity<ParameterizedErrorDTO> processNoAuthorityException(NoAuthorityException ex) {
        String errorMessage = messageSource.getMessage(ErrorCodeConstants.ERROR_NO_AUTHORITIES,
                new Object[]{ex.getUserName()}, ApplicationConstants.SYSTEM_LOCALE);
        ex.setMessage(errorMessage);
        log.error(errorMessage);
        return ResponseEntity.badRequest()
                .headers(httpHeaderCreator.createErrorHeader(ErrorCodeConstants.ERROR_NO_AUTHORITIES, ex.getUserName()))
                .body(ex.getErrorDTO());
    }

    /**
     * No data error handler
     */
    @ExceptionHandler(NoDataException.class)
    @ResponseBody
    public ResponseEntity<ParameterizedErrorDTO> processNoDataException(NoDataException ex) {
        String errorMessage = messageSource.getMessage(ErrorCodeConstants.ERROR_DATA_NOT_EXIST,
                new Object[]{ex.getId()}, ApplicationConstants.SYSTEM_LOCALE);
        ex.setMessage(errorMessage);
        log.error(errorMessage);
        return ResponseEntity.badRequest()
                .headers(httpHeaderCreator.createErrorHeader(ErrorCodeConstants.ERROR_DATA_NOT_EXIST, ex.getId()))
                .body(ex.getErrorDTO());
    }

    /**
     * Custom Error handler
     */
    @ExceptionHandler(CustomParameterizedException.class)
    @ResponseBody
    public ResponseEntity<ParameterizedErrorDTO> processCustomParameterizedException(CustomParameterizedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().headers(httpHeaderCreator.createErrorHeader(ex.getCode(), ex.getParams()))
                .body(ex.getErrorDTO());
    }

    /**
     * Spring security access denied handler
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> processAccessDeniedException(AccessDeniedException ex) {
        String warnMessage = ex.getMessage();
        log.warn(warnMessage);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .headers(httpHeaderCreator.createErrorHeader(ErrorCodeConstants.WARN_ACCESS_DENIED))
                .body(new ErrorDTO(ErrorCodeConstants.WARN_ACCESS_DENIED, warnMessage));
    }

    /**
     * Method not supported handler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> processMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String warnMessage = ex.getMessage();
        log.warn(warnMessage);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(httpHeaderCreator.createErrorHeader(ErrorCodeConstants.WARN_METHOD_NOT_SUPPORTED))
                .body(new ErrorDTO(ErrorCodeConstants.WARN_METHOD_NOT_SUPPORTED, warnMessage));
    }

    /**
     * Concurrency failure handler
     */
    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> processConcurrencyException(ConcurrencyFailureException ex) {
        String errorMessage = messageSource.getMessage(ErrorCodeConstants.ERROR_CONCURRENCY_EXCEPTION, null,
                ApplicationConstants.SYSTEM_LOCALE);
        log.error(errorMessage, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .headers(httpHeaderCreator.createErrorHeader(ErrorCodeConstants.ERROR_CONCURRENCY_EXCEPTION))
                .body(new ErrorDTO(ErrorCodeConstants.ERROR_CONCURRENCY_EXCEPTION, errorMessage));
    }

    /**
     * Exception handler
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> processException(Throwable throwable) {
        String errorMessage = messageSource.getMessage(ErrorCodeConstants.ERROR_SYSTEM_EXCEPTION, null,
                ApplicationConstants.SYSTEM_LOCALE);
        log.error(errorMessage, throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(httpHeaderCreator.createErrorHeader(ErrorCodeConstants.ERROR_SYSTEM_EXCEPTION))
                .body(new ErrorDTO(ErrorCodeConstants.ERROR_SYSTEM_EXCEPTION, errorMessage));
    }

    private ErrorDTO processFieldErrors(List<FieldError> fieldErrors) {
        List<FieldError> newFieldErrors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String defaultMessage = "";
            if (StringUtils.isNotEmpty(fieldError.getDefaultMessage())) {
                defaultMessage = fieldError.getDefaultMessage();
            } else if (fieldError.getCodes() != null) {
                List<String> errorCodes = Arrays.asList(fieldError.getCodes());
                defaultMessage = messageSource.getMessage(errorCodes.get(0), fieldError.getArguments(),
                        ApplicationConstants.SYSTEM_LOCALE);
            }

            FieldError newFieldError = new FieldError(fieldError.getObjectName(), fieldError.getField(),
                    fieldError.getRejectedValue(), true, fieldError.getCodes(), fieldError.getArguments(),
                    defaultMessage);
            newFieldErrors.add(newFieldError);
        }
        return new ErrorDTO(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR, messageSource
                .getMessage(ErrorCodeConstants.WARN_FIELDS_VALIDATION_ERROR, null, ApplicationConstants.SYSTEM_LOCALE),
                newFieldErrors);
    }
}

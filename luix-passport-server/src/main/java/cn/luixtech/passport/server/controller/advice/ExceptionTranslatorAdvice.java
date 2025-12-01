package cn.luixtech.passport.server.controller.advice;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.luixtech.springbootframework.component.MessageCreator;
import com.luixtech.utilities.exception.*;
import com.luixtech.utilities.response.Result;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * <p>
 * Exception list refer to {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}
 */
@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class ExceptionTranslatorAdvice {
    private static final String         ILLEGAL_ARGUMENTS_LOG = "Found illegal request arguments: ";
    private final        MessageCreator messageCreator;

    /**
     * JSR 303 bean validation exception handler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
        String msg = objectErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(msg));
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleBindException(BindException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        String warnMessage = handleFieldErrors(ex.getBindingResult().getFieldErrors());
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(warnMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(MismatchedInputException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleMismatchedInputException(MismatchedInputException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleNumberFormatException(NumberFormatException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(ex.getMessage()));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        return ResponseEntity.badRequest().body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(DuplicationException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleDuplicationException(DuplicationException ex) {
        log.warn(ILLEGAL_ARGUMENTS_LOG, ex);
        // Http status: 400
        String message = messageCreator.getMessage("IA1101", ex.getFieldMap());
        return ResponseEntity.badRequest().body(Result.illegalArgument(message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleIncorrectCredentialsException(BadCredentialsException ex) {
        log.warn("Incorrect user credentials: {}", ex.getMessage());
        // Http status: 400
        String message = messageCreator.getMessage("UE5006");
        return ResponseEntity.badRequest().body(Result.illegalArgument(message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied for requesting improper resource");
        // Http status: 403
        String message = messageCreator.getMessage("UE1010");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.dataNotFound(message));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: ", ex);
        // Http status: 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.dataNotFound( ex.getMessage()));
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleDataNotFoundException(DataNotFoundException ex) {
        log.warn("Data not found: ", ex);
        // Http status: 404
        String message = messageCreator.getMessage("IA1002", ex.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.dataNotFound(message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Found invalid request method: ", ex);
        // Http status: 405
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: ", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleConcurrencyFailureException(ConcurrencyFailureException ex) {
        log.error("Found concurrency exception: ", ex);
        // Http status: 409
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Result.serverError(ex.getMessage()));
    }

    @ExceptionHandler(BizIllegalParamException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleBizIllegalParamException(BizIllegalParamException ex) {
        log.warn("Found BizIllegalParamException: ", ex);
        // 特意返回 Http status: 200
        return ResponseEntity.ok().body(Result.illegalArgument(ex.getMessage()));
    }

    @ExceptionHandler(InvocationTimeoutException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleInvocationTimeoutException(InvocationTimeoutException ex) {
        log.error("Found invocation timeout: ", ex);
        // Http status: 500
        String message = messageCreator.getMessage("SE1003", ex.getTimeoutInMs());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.invocationTimeout(message));
    }

    @ExceptionHandler(InvocationException.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleInvocationErrorException(InvocationException ex) {
        log.error("Found invocation exception: ", ex);
        // Http status: 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.invocationError(ex.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Result<Void>> handleException(Throwable throwable) {
        log.error("Found exception: ", throwable);
        // Http status: 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.serverError(throwable.getMessage()));
    }

    private String handleFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.toString();
    }
}

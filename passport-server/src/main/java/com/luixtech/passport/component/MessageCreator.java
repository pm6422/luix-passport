package com.luixtech.passport.component;

import com.luixtech.passport.config.ApplicationConstants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MessageCreator {
    private final MessageSource messageSource;

    public String getMessage(String code, Object... arguments) {
        return messageSource.getMessage(code, arguments, ApplicationConstants.SYSTEM_LOCALE);
    }
}

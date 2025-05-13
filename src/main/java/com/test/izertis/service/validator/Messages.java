package com.test.izertis.service.validator;


import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Messages {

    private final MessageSourceAccessor accessor;

    public Messages(MessageSource messageSource) {
        this.accessor = new MessageSourceAccessor(messageSource, LocaleContextHolder.getLocale());
    }

    public String get(MessageSourceResolvable code) {
        return accessor.getMessage(code, LocaleContextHolder.getLocale());
    }

    public Optional<String> getOptional(String code) {
        String message = accessor.getMessage(code, "", LocaleContextHolder.getLocale());
        return message.isEmpty() ? Optional.empty() : Optional.of(message);
    }

}

package org.infinity.passport.converter;

import com.github.dozermapper.core.DozerConverter;

public class FirstNameConverter extends DozerConverter<String, String> {

    public FirstNameConverter() {
        super(String.class, String.class);
    }

    @Override
    public String convertTo(String source, String destination) {
        return source;
    }

    @Override
    public String convertFrom(String source, String destination) {
        if ("louis".equalsIgnoreCase(source)) {
            return "liu";
        }
        return null;
    }
}

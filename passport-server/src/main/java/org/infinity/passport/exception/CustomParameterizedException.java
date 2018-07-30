package org.infinity.passport.exception;

import org.infinity.passport.dto.ParameterizedErrorDTO;

/**
 * Custom, parameterized exception, which can be translated on the client side.
 * For example:
 * 
 * <pre>
 * throw new CustomParameterizedException(&quot;myCustomError&quot;, &quot;hello&quot;, &quot;world&quot;);
 * </pre>
 * 
 * Can be translated with:
 * 
 * <pre>
 * "error.myCustomError" :  "The server says {{params[0]}} to {{params[1]}}"
 * </pre>
 */
public class CustomParameterizedException extends RuntimeException {

    private static final long serialVersionUID = -8337754906302089776L;

    private final String      code;

    private final String      message;

    private final Object[]    params;

    public CustomParameterizedException(String code, String message, Object... params) {
        super(message);
        this.code = code;
        this.message = message;
        this.params = params;
    }

    public String getCode() {
        return code;
    }

    public Object[] getParams() {
        return params;
    }

    public ParameterizedErrorDTO getErrorDTO() {
        return new ParameterizedErrorDTO(message, params);
    }
}

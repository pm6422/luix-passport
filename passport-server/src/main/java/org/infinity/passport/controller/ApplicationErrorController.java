package org.infinity.passport.controller;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Basic Controller which is called for unhandled errors
 */
@Controller
@AllArgsConstructor
public class ApplicationErrorController implements ErrorController {

    /**
     * The error mapping URL
     */
    private static final String          ERROR_PATH = "/error";
    /**
     * Error Attributes in the Application
     */
    private final        ErrorAttributes errorAttributes;

    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
//    @Override
//    public String getErrorPath() {
//        return ERROR_PATH;
//    }

    /**
     * Supports the HTML Error View
     *
     * @param request http servlet request
     * @return ModelAndView
     */
    @RequestMapping(value = ERROR_PATH, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request) {
        return new ModelAndView("errors/error", getErrorAttributes(request, false));
    }

    /**
     * Supports other formats like JSON, XML
     *
     * @param request http servlet request
     * @return error information
     */
    @RequestMapping(value = ERROR_PATH, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(body, status);
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equalsIgnoreCase(parameter);
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        WebRequest requestAttributes = new ServletWebRequest(request);
        ErrorAttributeOptions options = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.BINDING_ERRORS);
        if (includeStackTrace) {
            options.including(ErrorAttributeOptions.Include.STACK_TRACE);
        }
        return this.errorAttributes.getErrorAttributes(requestAttributes, options);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception ex) {
                // leave blank intentionally
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
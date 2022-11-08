package org.s3s3l.matrix.configuration;

import javax.servlet.http.HttpServletRequest;

import org.s3s3l.matrix.utils.bean.exception.HttpRequestException;
import org.s3s3l.matrix.utils.bean.exception.VerifyException;
import org.s3s3l.matrix.utils.bean.web.JsonResult;
import org.s3s3l.matrix.utils.web.ResultHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler {

    @ResponseBody
    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<?> resolveHttpRequestException(HttpServletRequest request, Object handler, Exception e) {
        HttpRequestException ex = (HttpRequestException) e;
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(handleException(request, e), HttpStatus.valueOf(ex.getHttpStatus()));
    }

    @ResponseBody
    @ExceptionHandler(VerifyException.class)
    public ResponseEntity<?> resolveVerifyException(HttpServletRequest request, Object handler, Exception e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(handleException(request, e), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<?> resolveException(HttpServletRequest request, Object handler, Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private JsonResult<?> handleException(HttpServletRequest request, Exception ex) {
        return ResultHelper.fail(ex.getMessage());
    }
}
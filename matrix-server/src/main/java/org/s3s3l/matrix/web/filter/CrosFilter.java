package org.s3s3l.matrix.web.filter;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.s3s3l.matrix.utils.web.RequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("crosFilter")
public class CrosFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper((HttpServletResponse) res);
        HttpServletRequest request = new RequestWrapper((HttpServletRequest) req);
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
            response.setHeader("Access-Control-Allow-Method", "*");

            boolean isJsonBody = request.getContentType() == null ? false
                    : request.getContentType()
                            .toLowerCase()
                            .contains(ContentType.APPLICATION_JSON.getMimeType()
                                    .toLowerCase());
            StringBuilder sb = new StringBuilder();
            if (isJsonBody) {
                try (BufferedReader reader = request.getReader()) {
                    char[] buf = new char[1];
                    while (reader.read(buf) != -1) {
                        sb.append(buf);
                    }
                }
            }
            log.info(
                    "Request. host: {}; path: {}; query string: {}; request body: {}", request.getHeader("Host"),
                    request.getPathInfo(),
                    request.getQueryString(), sb.toString());
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            log.error("request error", e);
            return;
        }

        chain.doFilter(request, response);

        response.copyBodyToResponse();
    }

}

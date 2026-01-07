package com.trainerworkload.infrastructure.logging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
public class TransactionIdFilter extends OncePerRequestFilter {
    private static final String TX_ID = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var wrappedRequest = new ContentCachingRequestWrapper(request, 8192);
        var wrappedResponse = new ContentCachingResponseWrapper(response);
        String transactionId = request.getHeader("X-Transaction-Id");
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put(TX_ID, transactionId);
        log.info("Incoming request: {} {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI());
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logBody(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding(),
                    "Request body");

            logBody(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding(),
                    "Response body");

            log.info("Response status: {}", wrappedResponse.getStatus());
            wrappedResponse.copyBodyToResponse();
            MDC.remove(TX_ID);
        }
    }

    private void logBody(byte[] bodyBytes, String charset, String prefix) throws UnsupportedEncodingException {
        if (bodyBytes.length > 0) {
            String body = new String(bodyBytes, charset);
            log.info("{}: {}", prefix, body);
        }
    }
}

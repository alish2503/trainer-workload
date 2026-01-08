package com.trainerworkload.infrastructure.filter;

import com.trainerworkload.infrastructure.logging.filter.TransactionIdFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TransactionIdFilter transactionIdFilter;

    @Test
    void assignsNewTransactionIdWhenHeaderIsMissing() throws Exception {
        when(request.getHeader("X-Transaction-Id")).thenReturn(null);
        transactionIdFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(any(), any());
        assertNull(MDC.get("transactionId"));
    }

    @Test
    void usesExistingTransactionIdFromHeader() throws Exception {
        when(request.getHeader("X-Transaction-Id")).thenReturn("existing-id");
        doAnswer(invocation -> {
            assertEquals("existing-id", MDC.get("transactionId"));
            return null;
        }).when(filterChain).doFilter(any(), any());
        transactionIdFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
    }

    @Test
    void removesTransactionIdFromMDCAfterProcessing() throws Exception {
        when(request.getHeader("X-Transaction-Id")).thenReturn("test-id");
        transactionIdFilter.doFilter(request, response, filterChain);
        assertNull(MDC.get("transactionId"));
    }
}


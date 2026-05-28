package com.example.trainerworkload.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class TransactionLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingFilter.class);
    private static final String TX_HEADER = "X-Transaction-Id";
    private static final String MDC_KEY = "transactionId";

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String transactionId = request.getHeader(TX_HEADER);
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_KEY, transactionId);
        response.setHeader(TX_HEADER, transactionId);

        log.info("[TX-START] {} {} | transactionId={}",
                request.getMethod(), request.getRequestURI(), transactionId);

        try {
            chain.doFilter(request, response);
            log.info("[TX-END] status={} | transactionId={}", response.getStatus(), transactionId);
        } catch (Exception e) {
            log.error("[TX-ERROR] {} | transactionId={}", e.getMessage(), transactionId);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}

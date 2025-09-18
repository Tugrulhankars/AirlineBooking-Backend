package org.airlinebooking.airlinebooking.filter;

import io.micrometer.core.instrument.util.IOUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.airlinebooking.airlinebooking.filter.wrapper.RequestWrapper;
import org.airlinebooking.airlinebooking.filter.wrapper.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.airlinebooking.airlinebooking.filter.TraceGeneratorService.X_TRACE_ID;


@Component
public class LoggingFilter extends OncePerRequestFilter {
    private final TraceGeneratorService traceGeneratorService;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingFilter.class);

    public LoggingFilter(TraceGeneratorService traceGeneratorService) {
        this.traceGeneratorService = traceGeneratorService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestWrapper requestWrapper=new RequestWrapper(request);
        ResponseWrapper responseWrapper=new ResponseWrapper(response);
        logRequest(requestWrapper);
        traceGeneratorService.generateTraceId(request);
        setResponseHeader(responseWrapper);
        logger.info(requestLogFormatString(requestWrapper));
        filterChain.doFilter(requestWrapper,responseWrapper);
        logger.info(responseLogFormatString(responseWrapper));

    }

    private void logRequest(RequestWrapper requestWrapper){
        logger.info("request serverName: %s, request method: %s, request uri: %s, request headers: %s"
                ,requestWrapper.getServerName(),requestWrapper.getMethod(),requestWrapper.getRequestURI(),requestWrapper.getAllHeaders());
        System.out.println("request serverName: "+requestWrapper.getServerName());
        System.out.println("Log Request Çalıştırıldı");

    }
    private String requestLogFormatString(RequestWrapper request)throws IOException{
        System.out.println("request Log Format String çalıştı");
        return String.format("## %s ## Request Method: %s, Request Uri: %s, Request TraceId: %s, Request Headers: %s, Request Body: %s",
                request.getServerName(),
                request.getMethod(),
                request.getRequestURI(),
                request.getAttribute(X_TRACE_ID),
                request.getAllHeaders(),
                RequestWrapper.body);

    }

    private String responseLogFormatString(ResponseWrapper responseWrapper) {
        System.out.println("Response Log Format String Çalıştırıldı");
        return String.format("Response Status: %s, Response Headers: %s, Response TraceId: %s, Response Body, %s",
                responseWrapper.getStatus(),
                responseWrapper.getAllHeaders(),
                responseWrapper.getHeader(X_TRACE_ID));
                //IOUtils.toString(responseWrapper.getCopyBody(), responseWrapper.getCharacterEncoding()));
    }

    private void setResponseHeader(HttpServletResponse response){
        response.addHeader(X_TRACE_ID, MDC.get(X_TRACE_ID));
        System.out.println("Set Response Header Çalıştırıldı TraceId Set edildi");
    }
}

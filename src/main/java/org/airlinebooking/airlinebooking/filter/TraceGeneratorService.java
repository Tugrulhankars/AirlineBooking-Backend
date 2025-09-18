package org.airlinebooking.airlinebooking.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TraceGeneratorService {
    public static final String X_TRACE_ID = "X-Trace-Id";
    public void generateTraceId(HttpServletRequest request){
        String traceId= UUID.randomUUID().toString();
        MDC.put(X_TRACE_ID,traceId);
    }
}

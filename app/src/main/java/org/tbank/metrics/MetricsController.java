package org.tbank.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MetricsController {

    private final MeterRegistry meterRegistry;

    @GetMapping("/test")
    public ResponseEntity<String> testMetrics(@RequestHeader("User-ID") String userId) {
        logWithUserId(userId, "Received a test request");

        return ResponseEntity.ok("Log created");
    }

    @GetMapping("/custom")
    public String incrementCustomMetric() {
        meterRegistry.counter("custom_requests_total", "endpoint", "/custom")
                .increment();

        return "Custom metric incremented!";
    }

    @GetMapping("/recursive")
    public void recursiveCall() {
        // This method intentionally causes a StackOverflowError
        recursiveCall();
    }

    @GetMapping("/oom")
    public void outOfMemory() {
        // This method intentionally causes an OutOfMemoryError
        List<String> list = new ArrayList<>();

        while (true) list.add("OutOfMemoryError");
    }

    private void logWithUserId(String userId, String message) {
        MDC.put("userId", userId);
        log.info(message);
        MDC.clear();
    }
}

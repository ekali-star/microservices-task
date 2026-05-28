package com.example.gymcrm.client;

import com.example.gymcrm.filter.TransactionLoggingFilter;
import com.example.gymcrm.model.ActionType;
import com.example.gymcrm.model.Training;
import com.example.gymcrm.security.JwtService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class WorkloadClient {

    private static final Logger log = LoggerFactory.getLogger(WorkloadClient.class);
    private static final String CIRCUIT_BREAKER_NAME = "workload-service";

    private final RestTemplate restTemplate;
    private final JwtService jwtService;

    @Value("${workload.service.url}")
    private String workloadServiceUrl;

    public WorkloadClient(RestTemplate restTemplate, JwtService jwtService) {
        this.restTemplate = restTemplate;
        this.jwtService = jwtService;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "notifyFallback")
    public void notifyTrainingAdded(Training training) {
        sendNotification(training, ActionType.ADD);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "notifyFallback")
    public void notifyTrainingDeleted(Training training) {
        sendNotification(training, ActionType.DELETE);
    }

    private void sendNotification(Training training, ActionType actionType) {
        Map<String, Object> payload = buildPayload(training, actionType);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, buildHeaders());

        String url = workloadServiceUrl + "/api/workload";
        log.debug("Sending workload notification: trainer={}, action={}",
                training.getTrainer().getUser().getUsername(), actionType);

        ResponseEntity<Void> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Void.class);

        log.info("Workload notification sent: trainer={}, action={}, status={}",
                training.getTrainer().getUser().getUsername(),
                actionType, response.getStatusCode());
    }

    @SuppressWarnings("unused")
    private void notifyFallback(Training training, Throwable t) {
        log.warn("Workload service unavailable. Training id={}, trainer={} — workload NOT updated. Reason: {}",
                training.getId(),
                training.getTrainer().getUser().getUsername(),
                t.getMessage());
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtService.generateToken("gymcrm-service"));

        String txId = MDC.get(TransactionLoggingFilter.TX_ID_MDC_KEY);
        if (txId != null) {
            headers.set(TransactionLoggingFilter.TX_ID_HEADER, txId);
        }

        return headers;
    }

    private Map<String, Object> buildPayload(Training training, ActionType actionType) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("trainerUsername", training.getTrainer().getUser().getUsername());
        payload.put("trainerFirstName", training.getTrainer().getUser().getFirstName());
        payload.put("trainerLastName", training.getTrainer().getUser().getLastName());
        payload.put("isActive", training.getTrainer().getUser().getIsActive());
        payload.put("trainingDate", training.getTrainingDate().toString());
        payload.put("trainingDuration", training.getTrainingDuration());
        payload.put("actionType", actionType.name());
        return payload;
    }
}

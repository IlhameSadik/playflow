package com.example.demo.controller;
import com.example.demo.entity.FraudAlertStatus;
import com.example.demo.dto.FraudAlertResponse;
import com.example.demo.entity.FraudAlert;
import com.example.demo.service.FraudAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/admin/fraud-alerts")
public class FraudAlertController {

    private final FraudAlertService fraudAlertService;

    public FraudAlertController(FraudAlertService fraudAlertService) {
        this.fraudAlertService = fraudAlertService;
    }

    @GetMapping
    public ResponseEntity<List<FraudAlertResponse>> getAllAlerts() {

        List<FraudAlertResponse> response =
                fraudAlertService.getAllAlerts()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/open")
    public ResponseEntity<List<FraudAlertResponse>> getOpenAlerts() {

        List<FraudAlertResponse> response =
                fraudAlertService.getOpenAlerts()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    private FraudAlertResponse toResponse(FraudAlert alert) {

        return new FraudAlertResponse(
                alert.getId(),
                alert.getTransaction().getId(),
                alert.getTransaction().getAmount(),
                alert.getReason(),
                alert.getStatus().name(),
                alert.getCreatedAt()
        );
    }
@PutMapping("/{id}/status")
public ResponseEntity<FraudAlertResponse> updateStatus(
        @PathVariable Long id,
        @RequestParam FraudAlertStatus status,
        Authentication authentication) {

    String adminUsername = authentication.getName();

    FraudAlert alert =
            fraudAlertService.updateStatus(
                    id,
                    status,
                    adminUsername
            );

    return ResponseEntity.ok(toResponse(alert));
}
}
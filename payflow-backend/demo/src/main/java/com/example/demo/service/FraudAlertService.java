package com.example.demo.service;
import com.example.demo.entity.FraudAlertStatus;
import com.example.demo.entity.FraudAlert;
import com.example.demo.repository.FraudAlertRepository;
import org.springframework.stereotype.Service;
import com.example.demo.exception.BusinessException;
import java.util.List;

@Service
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final AuditLogService auditLogService;
    public FraudAlertService(
            FraudAlertRepository fraudAlertRepository,
            AuditLogService auditLogService) {
        this.fraudAlertRepository = fraudAlertRepository;
        this.auditLogService = auditLogService;
    }

    public List<FraudAlert> getOpenAlerts() {
      return fraudAlertRepository
        .findByStatusOrderByCreatedAtDesc(
                FraudAlertStatus.OPEN
        );
    }

    public List<FraudAlert> getAllAlerts() {
        return fraudAlertRepository.findAll();
    }
public FraudAlert updateStatus(
        Long alertId,
        FraudAlertStatus status,
        String adminUsername)  {

    FraudAlert alert =
            fraudAlertRepository.findById(alertId)
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Fraud alert not found"));

    if (status != FraudAlertStatus.REVIEWED &&
            status != FraudAlertStatus.DISMISSED) {

        throw new BusinessException(
                "Status must be REVIEWED or DISMISSED");
    }

    alert.setStatus(status);
    auditLogService.createLog(
        "FRAUD_ALERT_" + status.name(),
        adminUsername,
        "Fraud alert " + alertId
                + " marked as " + status.name()
);

    return fraudAlertRepository.save(alert);
}
}
package Com.Spacefinders.Service;

import Com.Spacefinders.Entity.AuditLog;
import Com.Spacefinders.Enums.ActionType;
import Com.Spacefinders.Repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {
    @Autowired
    private AuditRepository auditRepository;
    public void createAuditLog(ActionType actionType, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(actionType);
        auditLog.setAuditDescription(description);
        auditLog.setTimeStamp(LocalDateTime.now());

        auditRepository.save(auditLog);
    }
}

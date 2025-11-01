package Com.Spacefinders.Entity;

import Com.Spacefinders.Enums.ActionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 1000)
    private String auditDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    private String ipAddress;

    // Constructors
    public Audit() {
    }

    public Audit(Long auditId, Long userId, LocalDateTime timestamp,
                 String auditDescription, ActionType actionType, String ipAddress) {
        this.auditId = auditId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.auditDescription = auditDescription;
        this.actionType = actionType;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuditDescription() {
        return auditDescription;
    }

    public void setAuditDescription(String auditDescription) {
        this.auditDescription = auditDescription;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
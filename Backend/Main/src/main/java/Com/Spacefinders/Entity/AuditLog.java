package Com.Spacefinders.Entity;

import Com.Spacefinders.Enums.ActionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditId;

    @Enumerated(EnumType.STRING)
    ActionType action;

    @NotBlank
    String auditDescription;
    @NotNull
    LocalDateTime timeStamp;

    public Integer getId() {
        return auditId;
    }

    public void setId(Integer id) {
        this.auditId = auditId;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getAuditDescription() {
        return auditDescription;
    }

    public void setAuditDescription(String auditDescription) {
        this.auditDescription = auditDescription;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public AuditLog(ActionType action, String auditDescription, LocalDateTime timeStamp) {
        this.action = action;
        this.auditDescription = auditDescription;
        this.timeStamp = timeStamp;
    }

    public AuditLog() {
    }
}

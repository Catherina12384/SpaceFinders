package Com.Spacefinders.Entity;

import Com.Spacefinders.Enums.NotificationTarget;
import Com.Spacefinders.Enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @NotBlank(message = "Notification title should not be empty")
    private String notificationTitle;

    @NotBlank(message = "Notification message should not be empty")
    private String notificationMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType; // INFO, WARNING, SUCCESS, ERROR

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationTarget notificationTarget;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime notificationCreatedOn;

    @CreatedBy
    private final String notificationCreatedBy = "ADMIN";

    @Column(nullable = false)
    private boolean notificationIsRead = false;

    private LocalDateTime notificationReadOn;

    private Integer userId = null;

    public Notification(String notificationTitle, String notificationMessage, NotificationType notificationType,
                        NotificationTarget notificationTarget, LocalDateTime notificationCreatedOn,
                        boolean notificationIsRead, LocalDateTime notificationReadOn, Integer userId) {
        this.notificationTitle = notificationTitle;
        this.notificationMessage = notificationMessage;
        this.notificationType = notificationType;
        this.notificationTarget = notificationTarget;
        this.notificationCreatedOn = notificationCreatedOn;
        this.notificationIsRead = notificationIsRead;
        this.notificationReadOn = notificationReadOn;
        this.userId = userId;
    }

    public Notification() {
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationTarget getNotificationTarget() {
        return notificationTarget;
    }

    public void setNotificationTarget(NotificationTarget notificationTarget) {
        this.notificationTarget = notificationTarget;
    }

    public LocalDateTime getNotificationCreatedOn() {
        return notificationCreatedOn;
    }

    public void setNotificationCreatedOn(LocalDateTime notificationCreatedOn) {
        this.notificationCreatedOn = notificationCreatedOn;
    }

    public String getNotificationCreatedBy() {
        return notificationCreatedBy;
    }

    public boolean isNotificationIsRead() {
        return notificationIsRead;
    }

    public void setNotificationIsRead(boolean notificationIsRead) {
        this.notificationIsRead = notificationIsRead;
    }

    public LocalDateTime getNotificationReadOn() {
        return notificationReadOn;
    }

    public void setNotificationReadOn(LocalDateTime notificationReadOn) {
        this.notificationReadOn = notificationReadOn;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
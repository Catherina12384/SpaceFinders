package Com.Spacefinders.DTO.Request;

import Com.Spacefinders.Enums.NotificationTarget;
import Com.Spacefinders.Enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

public class NotificationRequest {

    @NotBlank(message = "Notification title should not be empty")
    private String notificationTitle;

    @NotBlank(message = "Notification message should not be empty")
    private String notificationMessage;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    private NotificationTarget notificationTarget;

    private Integer userId;

    public NotificationRequest(String notificationTitle, String notificationMessage,
                               NotificationType notificationType, NotificationTarget notificationTarget,
                               Integer userId) {
        this.notificationTitle = notificationTitle;
        this.notificationMessage = notificationMessage;
        this.notificationType = notificationType;
        this.notificationTarget = notificationTarget;
        this.userId = userId;
    }

    public NotificationRequest() {
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
package Com.Spacefinders.DTO.Response;

import Com.Spacefinders.Enums.NotificationType;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Integer notificationId;

    private Integer userId;

    private String notificationTitle;

    private String notificationMessage;

    private NotificationType notificationType;

    private LocalDateTime notificationCreatedOn;

    private final String notificationCreatedBy = "ADMIN";

    private boolean notificationIsRead;

    private LocalDateTime notificationReadOn;

    public NotificationResponse(Integer notificationId, Integer userId, String notificationTitle, String notificationMessage,
                                NotificationType notificationType, LocalDateTime notificationCreatedOn,
                                boolean notificationIsRead, LocalDateTime notificationReadOn) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.notificationTitle = notificationTitle;
        this.notificationMessage = notificationMessage;
        this.notificationType = notificationType;
        this.notificationCreatedOn = notificationCreatedOn;
        this.notificationIsRead = notificationIsRead;
        this.notificationReadOn = notificationReadOn;
    }

    public NotificationResponse() {
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
}
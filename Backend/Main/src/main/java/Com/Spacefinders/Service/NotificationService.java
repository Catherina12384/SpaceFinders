package Com.Spacefinders.Service;

import Com.Spacefinders.DTO.Request.NotificationRequest;
import Com.Spacefinders.DTO.Response.NotificationResponse;
import Com.Spacefinders.Entity.Notification;
import Com.Spacefinders.Enums.ActionType;
import Com.Spacefinders.Enums.NotificationTarget;
import Com.Spacefinders.Enums.NotificationType;
import Com.Spacefinders.Enums.UserRole;
import Com.Spacefinders.Exception.UserNotFoundException;
import Com.Spacefinders.Repository.NotificationRepository;
import Com.Spacefinders.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserRepository userRepository;

    //------------------------ ADMIN SERVICES ------------------------

   //Send notifications
    @Transactional
    public List<NotificationResponse> sendNotification(NotificationRequest request) {
        if (request.getNotificationTarget().equals(NotificationTarget.SPECIFIC)) {
            if (request.getUserId() == null) {
                auditService.createAuditLog(ActionType.ERROR,
                        "Tried to send a notification but userId not provided");
                throw new IllegalArgumentException("Notification for specific user needs user Id.");
            }

            boolean userExists = userRepository.existsById(request.getUserId());
            if (!userExists) {
                auditService.createAuditLog(ActionType.ERROR,
                        "Tried to send a notification to userId: " + request.getUserId() + " but does not exist");
                throw new UserNotFoundException("Unable to find user to send notification");
            }
        }

        List<Integer> userList = new ArrayList<>();

        if (request.getNotificationTarget().equals(NotificationTarget.CLIENT)) {
            userList = userRepository.findUserIdByUserRole(UserRole.CLIENT);
        } else if (request.getNotificationTarget().equals(NotificationTarget.HOST)) {
            userList = userRepository.findUserIdByUserRole(UserRole.HOST);
        } else if (request.getNotificationTarget().equals(NotificationTarget.ALL)) {
            userList = userRepository.findUserIdByUserRole(UserRole.HOST);
            userList.addAll(userRepository.findUserIdByUserRole(UserRole.CLIENT));
        } else {
            userList.add(request.getUserId());
        }

        if (userList.isEmpty()) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to send a notification but target not set");
            throw new UserNotFoundException("Unable to find target users to send notification");
        }

        List<Notification> notifications = new ArrayList<>();
        for (Integer user : userList) {
            Notification notification = new Notification();
            notification.setUserId(user);
            notification.setNotificationTitle(request.getNotificationTitle());
            notification.setNotificationMessage(request.getNotificationMessage());
            notification.setNotificationCreatedOn(LocalDateTime.now());
            notification.setNotificationType(request.getNotificationType());
            notification.setNotificationTarget(request.getNotificationTarget());
            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);

        List<NotificationResponse> responseList = new ArrayList<>();
        for (Notification notify : notifications) {
            responseList.add(convertToResponse(notify));
        }

        auditService.createAuditLog(ActionType.CREATE,
                "Notification sent to " + request.getNotificationTarget() + " - Total recipients: " + userList.size());

        return responseList;
    }

    //View all notifications ordered by creation date

    public List<NotificationResponse> viewAllNotifications() {
        List<Notification> notificationsList = notificationRepository.findAllByOrderByNotificationCreatedOnDesc();
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notificationsList) {
            response.add(convertToResponse(notification));
        }
        return response;
    }

    //View all notifications by type (INFO, WARNING, SUCCESS, ERROR)

    public List<NotificationResponse> viewAllNotificationsByType(NotificationType notificationType) {
        List<Notification> notificationsList =
                notificationRepository.findAllByNotificationTypeOrderByNotificationCreatedOnDesc(notificationType);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notificationsList) {
            response.add(convertToResponse(notification));
        }
        return response;
    }

    //View all notifications by target (ALL, HOST, CLIENT, SPECIFIC)

    public List<NotificationResponse> viewAllNotificationsByTarget(NotificationTarget notificationTarget) {
        List<Notification> notificationsList =
                notificationRepository.findAllByNotificationTargetOrderByNotificationCreatedOnDesc(notificationTarget);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notificationsList) {
            response.add(convertToResponse(notification));
        }
        return response;
    }

    //Count all notifications sent

    public Integer countAllNotifications() {
        return notificationRepository.countAllBy();
    }

    //Count notifications by read/unread status

    public Integer countAllNotificationsByReadStatus(boolean notificationIsRead) {
        return notificationRepository.countAllByNotificationIsRead(notificationIsRead);
    }

    //Count notifications by type

    public Integer countNotificationsByType(NotificationType notificationType) {
        return notificationRepository.countByNotificationType(notificationType);
    }

    // ------------------ USER SERVICES ------------------

    //View all their notifications

    public List<NotificationResponse> viewNotificationsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to view notifications by userId " + userId + " does not exist");
            throw new UserNotFoundException("No user with the userId exists to view notification");
        }

        List<Notification> notificationsList =
                notificationRepository.findByUserIdOrderByNotificationCreatedOnDesc(userId);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notificationsList) {
            response.add(convertToResponse(notification));
        }
        return response;
    }

    //View notifications by read/unread status

    public List<NotificationResponse> viewNotificationsByUserIdAndReadStatus(Integer userId, boolean notificationIsRead) {
        if (!userRepository.existsById(userId)) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to view notifications by userId " + userId + " does not exist");
            throw new UserNotFoundException("No user with the userId exists to view notification");
        }

        List<Notification> notificationsList =
                notificationRepository.findByUserIdAndNotificationIsReadOrderByNotificationCreatedOnDesc(userId, notificationIsRead);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notificationsList) {
            response.add(convertToResponse(notification));
        }
        return response;
    }

    //View notifications by type

    public List<NotificationResponse> viewNotificationsByUserIdAndType(Integer userId, NotificationType notificationType) {
        if (!userRepository.existsById(userId)) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to view notifications by userId " + userId + " does not exist");
            throw new UserNotFoundException("No user with the userId exists to view notification");
        }

        List<Notification> notificationsList =
                notificationRepository.findByUserIdAndNotificationTypeOrderByNotificationCreatedOnDesc(userId, notificationType);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notificationsList) {
            response.add(convertToResponse(notification));
        }
        return response;
    }

    //Count all notifications received

    public Integer countAllNotificationsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to count notifications by userId " + userId + " does not exist");
            throw new UserNotFoundException("No user with the userId exists");
        }
        return notificationRepository.countByUserId(userId);
    }

    //Count notifications by read/unread status

    public Integer countNotificationsByUserIdAndReadStatus(Integer userId, boolean notificationIsRead) {
        if (!userRepository.existsById(userId)) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to count notifications by userId " + userId + " does not exist");
            throw new UserNotFoundException("No user with the userId exists");
        }
        return notificationRepository.countByUserIdAndNotificationIsRead(userId, notificationIsRead);
    }

    //Get unread notifications count (for badge display)

    public Integer getUnreadNotificationsCount(Integer userId) {
        if (!userRepository.existsById(userId)) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to get unread count for userId " + userId + " does not exist");
            throw new UserNotFoundException("No user with the userId exists");
        }
        return notificationRepository.countByUserIdAndNotificationIsRead(userId, false);
    }

    //Mark a notification as read (can only be done once)
    @Transactional
    public NotificationResponse markNotificationAsRead(Integer notificationId, Integer userId) {
        if (!userRepository.existsById(userId)) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to mark notification as read by userId " + userId + " does not exist");
            throw new UserNotFoundException("No user with the userId exists");
        }

        Optional<Notification> notificationOpt =
                notificationRepository.findByNotificationIdAndUserId(notificationId, userId);

        if (notificationOpt.isEmpty()) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to mark notification " + notificationId + " as read but not found for userId " + userId);
            throw new IllegalArgumentException("Notification not found or does not belong to user");
        }

        Notification notification = notificationOpt.get();

        if (notification.isNotificationIsRead()) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to mark notification " + notificationId + " as read but already marked as read");
            throw new IllegalStateException("Notification is already marked as read and cannot be updated again");
        }

        notification.setNotificationIsRead(true);
        notification.setNotificationReadOn(LocalDateTime.now());
        notificationRepository.save(notification);

        return convertToResponse(notification);
    }

    // ==================== HELPER METHOD ====================

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse dto = new NotificationResponse();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUserId());
        dto.setNotificationTitle(notification.getNotificationTitle());
        dto.setNotificationMessage(notification.getNotificationMessage());
        dto.setNotificationType(notification.getNotificationType());
        dto.setNotificationCreatedOn(notification.getNotificationCreatedOn());
        dto.setNotificationIsRead(notification.isNotificationIsRead());
        dto.setNotificationReadOn(notification.getNotificationReadOn());
        return dto;
    }
}
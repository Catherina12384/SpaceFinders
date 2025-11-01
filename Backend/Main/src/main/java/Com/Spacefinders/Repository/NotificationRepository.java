package Com.Spacefinders.Repository;

import Com.Spacefinders.Entity.Notification;
import Com.Spacefinders.Enums.NotificationTarget;
import Com.Spacefinders.Enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // USERS - View notifications
    List<Notification> findByUserIdOrderByNotificationCreatedOnDesc(Integer userId);

    List<Notification> findByUserIdAndNotificationIsReadOrderByNotificationCreatedOnDesc(Integer userId, boolean notificationIsRead);

    List<Notification> findByUserIdAndNotificationTypeOrderByNotificationCreatedOnDesc(Integer userId, NotificationType notificationType);

    // USERS - Count notifications
    Integer countByUserIdAndNotificationIsRead(Integer userId, boolean notificationIsRead);

    Integer countByUserId(Integer userId);

    // USERS - Mark as read
    Optional<Notification> findByNotificationIdAndUserId(Integer notificationId, Integer userId);

    // ADMIN - View notifications
    List<Notification> findAllByOrderByNotificationCreatedOnDesc();

    List<Notification> findAllByNotificationTypeOrderByNotificationCreatedOnDesc(NotificationType notificationType);

    List<Notification> findAllByNotificationTargetOrderByNotificationCreatedOnDesc(NotificationTarget notificationTarget);

    // ADMIN - Count notifications
    Integer countAllBy();

    Integer countAllByNotificationIsRead(boolean notificationIsRead);

    Integer countByNotificationType(NotificationType notificationType);
}
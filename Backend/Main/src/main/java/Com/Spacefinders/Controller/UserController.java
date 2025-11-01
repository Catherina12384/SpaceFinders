package Com.Spacefinders.Controller;

import Com.Spacefinders.DTO.Request.*;
import Com.Spacefinders.DTO.Response.*;
import Com.Spacefinders.Enums.NotificationType;
import Com.Spacefinders.Service.ClientService;
import Com.Spacefinders.Service.NotificationService;
import Com.Spacefinders.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private NotificationService notificationService;


    // Add User (Signup)
    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse<UserResponse>> addUser(@Valid @RequestBody UserSignupRequest request) {
        UserResponse userResponse = userService.addUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User registered successfully", userResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Login User
    @PostMapping("/loginUser")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@Valid @RequestBody UserLoginRequest request) {
        LoginResponse loginResponse = userService.loginUser(request);
        ApiResponse<LoginResponse> response = new ApiResponse<>(true, "Login successful", loginResponse);
        return ResponseEntity.ok(response);
    }

    // View Profile
    @GetMapping("/viewProfile/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> viewProfile(@PathVariable int userId) {
        UserResponse userResponse = userService.viewProfile(userId);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User profile retrieved successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    // Update User
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable int userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse userResponse = userService.updateUser(userId, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User updated successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    // Delete User
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        ApiResponse<String> response = new ApiResponse<>(true, "User deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    // Reset Password
    @PutMapping("/resetPassword")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        ApiResponse<String> response = new ApiResponse<>(true, "Password reset successfully", null);
        return ResponseEntity.ok(response);
    }

    // View Complaints
    @GetMapping("/viewComplaints/{userId}")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> viewComplaints(@PathVariable int userId) {
        List<ComplaintResponse> complaints = clientService.viewComplaints(userId);
        ApiResponse<List<ComplaintResponse>> response = new ApiResponse<>(true, "Complaints retrieved successfully", complaints);
        return ResponseEntity.ok(response);
    }

    // Logout User
    @PostMapping("/logoutUser/{userId}")
    public ResponseEntity<ApiResponse<String>> logoutUser(@PathVariable int userId) {
        userService.logoutUser(userId);
        ApiResponse<String> response = new ApiResponse<>(true, "Logout successful", null);
        return ResponseEntity.ok(response);
    }


    // View All User's Notifications
    @GetMapping("/viewAllNotifications/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> viewNotificationsByUserId(
            @PathVariable Integer userId) {
        List<NotificationResponse> notifications = notificationService.viewNotificationsByUserId(userId);
        ApiResponse<List<NotificationResponse>> response = new ApiResponse<>(
                true,
                "Notifications retrieved successfully",
                notifications
        );
        return ResponseEntity.ok(response);
    }

    // View Notifications by Read Status
    @GetMapping("/viewNotificationByReadStatus/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> viewNotificationsByUserIdAndReadStatus(
            @PathVariable Integer userId,
            @RequestParam boolean isRead) {
        List<NotificationResponse> notifications =
                notificationService.viewNotificationsByUserIdAndReadStatus(userId, isRead);
        String status = isRead ? "read" : "unread";
        ApiResponse<List<NotificationResponse>> response = new ApiResponse<>(
                true,
                status.substring(0, 1).toUpperCase() + status.substring(1) + " notifications retrieved successfully",
                notifications
        );
        return ResponseEntity.ok(response);
    }

    // View Notifications by Type
    @GetMapping("/viewNotificationByType/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> viewNotificationsByUserIdAndType(
            @PathVariable Integer userId,
            @RequestParam NotificationType type) {
        List<NotificationResponse> notifications =
                notificationService.viewNotificationsByUserIdAndType(userId, type);
        ApiResponse<List<NotificationResponse>> response = new ApiResponse<>(
                true,
                "Notifications retrieved successfully for type: " + type,
                notifications
        );
        return ResponseEntity.ok(response);
    }

    // Count All User's Notifications
    @GetMapping("/count/allNotification/{userId}")
    public ResponseEntity<ApiResponse<Integer>> countAllNotificationsByUserId(
            @PathVariable Integer userId) {
        Integer count = notificationService.countAllNotificationsByUserId(userId);
        ApiResponse<Integer> response = new ApiResponse<>(
                true,
                "Total notifications count retrieved successfully",
                count
        );
        return ResponseEntity.ok(response);
    }

    // Count Notifications by Read Status
    @GetMapping("/count/NotificationbyReadStatus/{userId}")
    public ResponseEntity<ApiResponse<Integer>> countNotificationsByUserIdAndReadStatus(
            @PathVariable Integer userId,
            @RequestParam boolean isRead) {
        Integer count = notificationService.countNotificationsByUserIdAndReadStatus(userId, isRead);
        String status = isRead ? "read" : "unread";
        ApiResponse<Integer> response = new ApiResponse<>(
                true,
                "Total " + status + " notifications count retrieved successfully",
                count
        );
        return ResponseEntity.ok(response);
    }

//    // Get Unread Notifications Count (for badge)
//    @GetMapping("/count/unread/{userId}")
//    public ResponseEntity<ApiResponse<Integer>> getUnreadNotificationsCount(
//            @PathVariable Integer userId) {
//        Integer count = notificationService.getUnreadNotificationsCount(userId);
//        ApiResponse<Integer> response = new ApiResponse<>(
//                true,
//                "Unread notifications count retrieved successfully",
//                count
//        );
//        return ResponseEntity.ok(response);
//    }

    // Mark Notification as Read
    @PutMapping("/markNotificationAsRead/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> markNotificationAsRead(
            @PathVariable Integer notificationId,
            @RequestParam Integer userId) {
        NotificationResponse notification =
                notificationService.markNotificationAsRead(notificationId, userId);
        ApiResponse<NotificationResponse> response = new ApiResponse<>(
                true,
                "Notification marked as read successfully",
                notification
        );
        return ResponseEntity.ok(response);
    }
}
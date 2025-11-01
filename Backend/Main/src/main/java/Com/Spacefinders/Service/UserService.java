//Audit log added

package Com.Spacefinders.Service;

import Com.Spacefinders.DTO.Request.*;
import Com.Spacefinders.DTO.Response.*;
import Com.Spacefinders.Entity.AuditLog;
import Com.Spacefinders.Entity.Booking;
import Com.Spacefinders.Entity.User;
import Com.Spacefinders.Enums.*;
import Com.Spacefinders.Exception.*;
import Com.Spacefinders.Repository.AuditRepository;
import Com.Spacefinders.Repository.BookingRepository;
import Com.Spacefinders.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private BookingRepository bookingRepository;

    // Add User (Signup) added auditLog
    public UserResponse addUser(UserSignupRequest request) {
        // Check if username already exists
        User existingUser = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (existingUser != null) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to create User but Username already exists UserName" +
                    " Tried: " + existingUser.getUsername());
            throw new DuplicateResourceException("Username already exists");
        }

        // Check if email already exists
        User existingEmail = userRepository.findByUserMail(request.getUserMail());
        if (existingEmail != null) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to create User but Email already registered Email " +
                    "Tried: " + existingEmail.getUserMail());
            throw new DuplicateResourceException("Email already registered");
        }

        // Check if phone already exists
        User existingPhone = userRepository.findByUserPhone(request.getUserPhone());
        if (existingPhone != null) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to create User but Phone number already registered " +
                    "Phone Tried: " + existingPhone.getUserPhone());
            throw new DuplicateResourceException("Phone number already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setUserMail(request.getUserMail());
        user.setUserPhone(request.getUserPhone());
        user.setUserAddress(request.getUserAddress());
        user.setUserRole(UserRole.valueOf(request.getUserRole()));
        user.setUserStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        auditService.createAuditLog(ActionType.CREATE,
                "User registered successfully, UserName: " + savedUser.getUsername());

        return convertToUserResponse(savedUser);
    }

    // Login User added auditLog
    public LoginResponse loginUser(UserLoginRequest request) {
        // Check if user exists
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (user == null) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to Login but User not found Username");
            throw new UserNotFoundException("User not found. Please register first.");
        }

        // Check if password matches
        if (!user.getPassword().equals(request.getPassword())) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to Login but User password Wrong, Tried Username: " + user.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Check user status
        if (user.getUserStatus() == UserStatus.BLOCKED) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to Login but account is blocked, Tried Username: " + user.getUsername());
            throw new UnauthorizedException("Your account is blocked. Contact admin.");
        }

        if (user.getUserStatus() == UserStatus.DELETED) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to Login but User account is deleted, Tried " +
                    "Username: " + user.getUsername());

            throw new UserNotFoundException("Account not found");
        }

        // Create audit log
        auditService.createAuditLog(ActionType.LOGIN, "User logged in, Username: " + user.getUsername());

        // Create login response
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getUserMail());
        response.setRole(user.getUserRole().toString());

        return response;
    }

    // View Profile added auditLog
    public UserResponse viewProfile(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        auditService.createAuditLog(ActionType.ERROR,
                "Tried to View user Details, Tried Username: " + user.getUsername());
        return convertToUserResponse(user);
    }

    // Update User added auditLog
    public UserResponse updateUser(int userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if new email already exists (and it's not the current user's email)
        if (!user.getUserMail().equals(request.getUserMail())) {
            User existingEmail = userRepository.findByUserMail(request.getUserMail());
            if (existingEmail != null) {
                auditService.createAuditLog(ActionType.ERROR, "Tried to update email but Email already registered, " +
                        "Tried Email: " + user.getUserMail());
                throw new DuplicateResourceException("Email already registered");
            }
        }

        // Check if new phone already exists (and it's not the current user's phone)
        if (!user.getUserPhone().equals(request.getUserPhone())) {
            User existingPhone = userRepository.findByUserPhone(request.getUserPhone());
            if (existingPhone != null) {
                auditService.createAuditLog(ActionType.ERROR, "Tried to update email but Phone number already " +
                        "registered, Tried Phone: " + user.getUserPhone());
                throw new DuplicateResourceException("Phone number already registered");
            }
        }

        // Update user details
        user.setUserMail(request.getUserMail());
        user.setUserPhone(request.getUserPhone());
        user.setUserAddress(request.getUserAddress());

        User updatedUser = userRepository.save(user);

        // Create audit log
        auditService.createAuditLog(ActionType.UPDATE, "User profile updated, Username" + user.getUserMail() + " User" +
                " Email: " + user.getUserMail() + " User Phone: " + user.getUserPhone());

        return convertToUserResponse(updatedUser);
    }

    // Forgot Password
    public UserResponse forgotPassword(String userName, String newPassword){
        User user = userRepository.findByUsernameIgnoreCase(userName);
        if(user == null){
            auditService.createAuditLog(ActionType.ERROR, "Tried changing the password using forgot password but user" +
                    " does not exists");
            throw new UserNotFoundException("User name not found");
        }

        if(newPassword.equals(user.getPassword())){
            auditService.createAuditLog(ActionType.ERROR, "Same password entered during forgot password");
            throw new InvalidCredentialsException("Same password entered as the previous one");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        auditService.createAuditLog(ActionType.UPDATE, "Password updated successfully using forgot password");

        return convertToUserResponse(user);

    }

    // Delete User (Soft Delete) added auditLog
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Booking> booking = bookingRepository.findById(userId);

        if(booking.isPresent()) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to delete but User has active booking Username: " + user.getUsername());
            throw new InvalidOperationException("User has an active booking");
        }

        if (user.getUserStatus() == UserStatus.DELETED) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to delete but User is already deleted Username: " + user.getUsername());
            throw new InvalidOperationException("User is already deleted");
        }

        // Soft delete
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);

        // Create audit log
        auditService.createAuditLog(ActionType.DELETE, "User account deleted Username: " + user.getUsername());
    }


    // Reset Password added auditLog
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (user == null) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to reset password but User not found Username");
            throw new UserNotFoundException("User not found");
        }

        // Check if old password matches
        if (!user.getPassword().equals(request.getOldPassword())) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to reset password but Old password is incorrect " +
                    "Username: " + user.getUsername());
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        // Update password
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        // Create audit log
        auditService.createAuditLog(ActionType.UPDATE, "Password reset successfully username: " + user.getUsername());
    }

    // Logout User added auditLog
    public void logoutUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Create audit log
        auditService.createAuditLog(ActionType.LOGOUT, "User logged out username: " + user.getUsername());
    }

    // Helper method to convert User to UserResponse
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setUserMail(user.getUserMail());
        response.setUserPhone(user.getUserPhone());
        response.setUserAddress(user.getUserAddress());
        response.setUserRole(user.getUserRole().toString());
        response.setUserStatus(user.getUserStatus().toString());
        return response;
    }

}
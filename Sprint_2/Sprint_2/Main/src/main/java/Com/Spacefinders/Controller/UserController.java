package Com.Spacefinders.Controller;

import Com.Spacefinders.DTO.Request.*;
import Com.Spacefinders.DTO.Response.*;
import Com.Spacefinders.Service.ClientService;
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
    public ResponseEntity<ApiResponse<UserResponse>> viewProfile(@PathVariable Long userId) {
        UserResponse userResponse = userService.viewProfile(userId);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User profile retrieved successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    // Update User
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse userResponse = userService.updateUser(userId, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User updated successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    // Delete User
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
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
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> viewComplaints(@PathVariable Long userId) {
        List<ComplaintResponse> complaints = clientService.viewComplaints(userId);
        ApiResponse<List<ComplaintResponse>> response = new ApiResponse<>(true, "Complaints retrieved successfully", complaints);
        return ResponseEntity.ok(response);
    }

    // Logout User
    @PostMapping("/logoutUser/{userId}")
    public ResponseEntity<ApiResponse<String>> logoutUser(@PathVariable Long userId) {
        userService.logoutUser(userId);
        ApiResponse<String> response = new ApiResponse<>(true, "Logout successful", null);
        return ResponseEntity.ok(response);
    }
}
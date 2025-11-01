package Com.Spacefinders.DTO.Request;

import jakarta.validation.constraints.*;

public class UserSignupRequest {

    @NotNull(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
    )
    private String password;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String userMail;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String userPhone;

    @NotNull(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String userAddress;

    @NotNull(message = "User role is required")
    @Pattern(regexp = "CLIENT|HOST", message = "User role must be either CLIENT or HOST")
    private String userRole;

    // Constructors
    public UserSignupRequest() {
    }

    public UserSignupRequest(String username, String password, String userMail,
                             String userPhone, String userAddress, String userRole) {
        this.username = username;
        this.password = password;
        this.userMail = userMail;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userRole = userRole;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
package Com.Spacefinders.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String userMail;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String userPhone;

    @NotNull(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String userAddress;

    // Constructors
    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String userMail, String userPhone, String userAddress) {
        this.userMail = userMail;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
    }

    // Getters and Setters
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
}
package Com.Spacefinders.DTO.Request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BlockUserRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "User status is required")
    @Pattern(regexp = "ACTIVE|BLOCKED", message = "User status must be ACTIVE or BLOCKED")
    private String userStatus;

    // Constructors
    public BlockUserRequest() {
    }

    public BlockUserRequest(Long userId, String userStatus) {
        this.userId = userId;
        this.userStatus = userStatus;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
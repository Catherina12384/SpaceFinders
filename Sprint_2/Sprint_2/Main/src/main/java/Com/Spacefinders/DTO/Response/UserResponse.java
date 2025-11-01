package Com.Spacefinders.DTO.Response;

public class UserResponse {

    private Long userId;
    private String username;
    private String userMail;
    private String userPhone;
    private String userAddress;
    private String userRole;
    private String userStatus;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(Long userId, String username, String userMail, String userPhone,
                        String userAddress, String userRole, String userStatus) {
        this.userId = userId;
        this.username = username;
        this.userMail = userMail;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userRole = userRole;
        this.userStatus = userStatus;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
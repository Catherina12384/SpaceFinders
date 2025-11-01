package Com.Spacefinders.Entity;

import Com.Spacefinders.Enums.UserRole;
import Com.Spacefinders.Enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "Password must be at least 8 characters Integer and contain at least one digit, one lowercase " +
                    "letter," +
                    " one uppercase letter, and one special character.")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String userMail;

    @Column(nullable = false, unique = true)
    private String userPhone;

    @Column(nullable = false)
    private String userAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole; //ADMIN, CLIENT, HOST

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus; //ACTIVE, DELETED, BLOCKED

    // Constructors
    public User() {
    }

    public User(Integer userId, String username, String password, String userMail,
                String userPhone, String userAddress, UserRole userRole, UserStatus userStatus) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.userMail = userMail;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userRole = userRole;
        this.userStatus = userStatus;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
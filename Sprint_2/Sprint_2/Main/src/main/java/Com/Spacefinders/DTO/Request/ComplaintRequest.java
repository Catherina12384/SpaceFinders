package Com.Spacefinders.DTO.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ComplaintRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long bookingId;

    @NotNull(message = "Complaint description is required")
    @Size(min = 10, max = 1000, message = "Complaint description must be between 10 and 1000 characters")
    private String complaintDescription;

    @NotNull(message = "Complaint type is required")
    @Pattern(regexp = "PROPERTY|USER|BOOKING|PAYMENT|OTHER",
            message = "Complaint type must be PROPERTY, USER, BOOKING, PAYMENT, or OTHER")
    private String complaintType;

    // Constructors
    public ComplaintRequest() {
    }

    public ComplaintRequest(Long userId, Long bookingId, String complaintDescription, String complaintType) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.complaintDescription = complaintDescription;
        this.complaintType = complaintType;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getComplaintDescription() {
        return complaintDescription;
    }

    public void setComplaintDescription(String complaintDescription) {
        this.complaintDescription = complaintDescription;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }
}
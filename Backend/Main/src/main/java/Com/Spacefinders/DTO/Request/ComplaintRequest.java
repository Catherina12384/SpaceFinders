package Com.Spacefinders.DTO.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ComplaintRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    private Integer bookingId;

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

    public ComplaintRequest(Integer userId, Integer bookingId, String complaintDescription, String complaintType) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.complaintDescription = complaintDescription;
        this.complaintType = complaintType;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
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
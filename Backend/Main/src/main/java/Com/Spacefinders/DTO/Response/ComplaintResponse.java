package Com.Spacefinders.DTO.Response;

import java.time.LocalDateTime;

public class ComplaintResponse {

    private Integer complaintId;
    private Integer userId;
    private String username;
    private Integer bookingId;
    private String complaintDescription;
    private String complaintStatus;
    private String complaintType;
    private LocalDateTime complaintDate;

    // Constructors
    public ComplaintResponse() {
    }

    public ComplaintResponse(Integer complaintId, Integer userId, String username, Integer bookingId,
                             String complaintDescription, String complaintStatus,
                             String complaintType, LocalDateTime complaintDate) {
        this.complaintId = complaintId;
        this.userId = userId;
        this.username = username;
        this.bookingId = bookingId;
        this.complaintDescription = complaintDescription;
        this.complaintStatus = complaintStatus;
        this.complaintType = complaintType;
        this.complaintDate = complaintDate;
    }

    // Getters and Setters
    public Integer getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Integer complaintId) {
        this.complaintId = complaintId;
    }

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

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public LocalDateTime getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(LocalDateTime complaintDate) {
        this.complaintDate = complaintDate;
    }
}
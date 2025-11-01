package Com.Spacefinders.Entity;

import Com.Spacefinders.Enums.ComplaintStatus;
import Com.Spacefinders.Enums.ComplaintType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaint")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer complaintId;

    @Column(nullable = false)
    private Integer userId;

    private Integer bookingId;

    @Column(nullable = false, length = 1000)
    private String complaintDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus complaintStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintType complaintType;

    @Column(nullable = false)
    private LocalDateTime complaintDate;

    // Constructors
    public Complaint() {
    }

    public Complaint(Integer complaintId, Integer userId, Integer bookingId,
                     String complaintDescription, ComplaintStatus complaintStatus,
                     ComplaintType complaintType, LocalDateTime complaintDate) {
        this.complaintId = complaintId;
        this.userId = userId;
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

    public ComplaintStatus getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(ComplaintStatus complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public ComplaintType getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(ComplaintType complaintType) {
        this.complaintType = complaintType;
    }

    public LocalDateTime getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(LocalDateTime complaintDate) {
        this.complaintDate = complaintDate;
    }
}
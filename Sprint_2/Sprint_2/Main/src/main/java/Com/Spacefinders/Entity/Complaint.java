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
    private Long complaintId;

    @Column(nullable = false)
    private Long userId;

    private Long bookingId;

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

    public Complaint(Long complaintId, Long userId, Long bookingId,
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
    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

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
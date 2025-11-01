package Com.Spacefinders.Entity;

import Com.Spacefinders.Enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false)
    private Long propertyId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate checkinDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private Boolean isPaymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus isBookingStatus;

    @Column(nullable = false)
    private Boolean hasExtraCot;

    @Column(nullable = false)
    private Boolean hasDeepClean;

    // Constructors
    public Booking() {
    }

    public Booking(Long bookingId, Long propertyId, Long userId, LocalDate checkinDate,
                   LocalDate checkoutDate, Boolean isPaymentStatus, BookingStatus isBookingStatus,
                   Boolean hasExtraCot, Boolean hasDeepClean) {
        this.bookingId = bookingId;
        this.propertyId = propertyId;
        this.userId = userId;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.isPaymentStatus = isPaymentStatus;
        this.isBookingStatus = isBookingStatus;
        this.hasExtraCot = hasExtraCot;
        this.hasDeepClean = hasDeepClean;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public Boolean getIsPaymentStatus() {
        return isPaymentStatus;
    }

    public void setIsPaymentStatus(Boolean isPaymentStatus) {
        this.isPaymentStatus = isPaymentStatus;
    }

    public BookingStatus getIsBookingStatus() {
        return isBookingStatus;
    }

    public void setIsBookingStatus(BookingStatus isBookingStatus) {
        this.isBookingStatus = isBookingStatus;
    }

    public Boolean getHasExtraCot() {
        return hasExtraCot;
    }

    public void setHasExtraCot(Boolean hasExtraCot) {
        this.hasExtraCot = hasExtraCot;
    }

    public Boolean getHasDeepClean() {
        return hasDeepClean;
    }

    public void setHasDeepClean(Boolean hasDeepClean) {
        this.hasDeepClean = hasDeepClean;
    }
}
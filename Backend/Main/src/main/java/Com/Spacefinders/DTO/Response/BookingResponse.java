package Com.Spacefinders.DTO.Response;

import java.time.LocalDate;

public class BookingResponse {

    private Integer bookingId;
    private Integer propertyId;
    private String propertyName;
    private String propertyImage;
    private String city;
    private Integer userId;
    private String username;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Boolean isPaymentStatus;
    private String isBookingStatus;
    private Boolean hasExtraCot;
    private Boolean hasDeepClean;

    // Constructors
    public BookingResponse() {
    }

    // Getters and Setters
    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyImage() {
        return propertyImage;
    }

    public void setPropertyImage(String propertyImage) {
        this.propertyImage = propertyImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getIsBookingStatus() {
        return isBookingStatus;
    }

    public void setIsBookingStatus(String isBookingStatus) {
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
package Com.Spacefinders.DTO.Request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingRequest {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkinDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkoutDate;

    @NotNull(message = "Extra cot status is required")
    private Boolean hasExtraCot;

    @NotNull(message = "Deep clean status is required")
    private Boolean hasDeepClean;

    // Constructors
    public BookingRequest() {
    }

    public BookingRequest(Long propertyId, Long userId, LocalDate checkinDate,
                          LocalDate checkoutDate, Boolean hasExtraCot, Boolean hasDeepClean) {
        this.propertyId = propertyId;
        this.userId = userId;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.hasExtraCot = hasExtraCot;
        this.hasDeepClean = hasDeepClean;
    }

    // Getters and Setters
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
package Com.Spacefinders.DTO.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public class SearchPropertyRequest {

    @NotNull(message = "Check-in date is required")
    private LocalDate checkinDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkoutDate;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer noOfGuests;

    private String city;
    private String state;
    private String country;

    // Constructors
    public SearchPropertyRequest() {
    }

    public SearchPropertyRequest(LocalDate checkinDate, LocalDate checkoutDate,
                                 Integer noOfGuests, String city, String state, String country) {
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.noOfGuests = noOfGuests;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    // Getters and Setters
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

    public Integer getNoOfGuests() {
        return noOfGuests;
    }

    public void setNoOfGuests(Integer noOfGuests) {
        this.noOfGuests = noOfGuests;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
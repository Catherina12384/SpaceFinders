package Com.Spacefinders.DTO.Request;

import jakarta.validation.constraints.*;

public class PropertyUpdateRequest {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Property name is required")
    @Size(min = 5, max = 100, message = "Property name must be between 5 and 100 characters")
    private String propertyName;

    @NotNull(message = "Property description is required")
    @Size(min = 20, max = 1000, message = "Property description must be between 20 and 1000 characters")
    private String propertyDescription;

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "Number of rooms must be at least 1")
    private Integer noOfRooms;

    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 1, message = "Number of bathrooms must be at least 1")
    private Integer noOfBathrooms;

    @NotNull(message = "Maximum number of guests is required")
    @Min(value = 1, message = "Maximum number of guests must be at least 1")
    private Integer maxNoOfGuests;

    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "100.0", message = "Price per day must be at least 100")
    private Double pricePerDay;

    private String imageURL;

    // Address fields
    @NotNull(message = "Building number is required")
    private String buildingNo;

    @NotNull(message = "Street is required")
    private String street;

    @NotNull(message = "City is required")
    private String city;

    @NotNull(message = "State is required")
    private String state;

    @NotNull(message = "Country is required")
    private String country;

    @NotNull(message = "Postal code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Postal code must be 6 digits")
    private String postalCode;

    // Amenities
    @NotNull(message = "WiFi availability is required")
    private Boolean hasWifi;

    @NotNull(message = "Parking availability is required")
    private Boolean hasParking;

    @NotNull(message = "Pool availability is required")
    private Boolean hasPool;

    @NotNull(message = "AC availability is required")
    private Boolean hasAc;

    @NotNull(message = "Heater availability is required")
    private Boolean hasHeater;

    @NotNull(message = "Pet friendly status is required")
    private Boolean hasPetFriendly;

    @NotNull(message = "Property status is required")
    @Pattern(regexp = "AVAILABLE|UNAVAILABLE|UNDER_MAINTENANCE",
            message = "Property status must be AVAILABLE, UNAVAILABLE, or UNDER_MAINTENANCE")
    private String propertyStatus;

    // Constructors
    public PropertyUpdateRequest() {
    }

    // Getters and Setters (similar to PropertyRequest)
    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public Integer getNoOfRooms() {
        return noOfRooms;
    }

    public void setNoOfRooms(Integer noOfRooms) {
        this.noOfRooms = noOfRooms;
    }

    public Integer getNoOfBathrooms() {
        return noOfBathrooms;
    }

    public void setNoOfBathrooms(Integer noOfBathrooms) {
        this.noOfBathrooms = noOfBathrooms;
    }

    public Integer getMaxNoOfGuests() {
        return maxNoOfGuests;
    }

    public void setMaxNoOfGuests(Integer maxNoOfGuests) {
        this.maxNoOfGuests = maxNoOfGuests;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public Boolean getHasParking() {
        return hasParking;
    }

    public void setHasParking(Boolean hasParking) {
        this.hasParking = hasParking;
    }

    public Boolean getHasPool() {
        return hasPool;
    }

    public void setHasPool(Boolean hasPool) {
        this.hasPool = hasPool;
    }

    public Boolean getHasAc() {
        return hasAc;
    }

    public void setHasAc(Boolean hasAc) {
        this.hasAc = hasAc;
    }

    public Boolean getHasHeater() {
        return hasHeater;
    }

    public void setHasHeater(Boolean hasHeater) {
        this.hasHeater = hasHeater;
    }

    public Boolean getHasPetFriendly() {
        return hasPetFriendly;
    }

    public void setHasPetFriendly(Boolean hasPetFriendly) {
        this.hasPetFriendly = hasPetFriendly;
    }

    public String getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(String propertyStatus) {
        this.propertyStatus = propertyStatus;
    }
}
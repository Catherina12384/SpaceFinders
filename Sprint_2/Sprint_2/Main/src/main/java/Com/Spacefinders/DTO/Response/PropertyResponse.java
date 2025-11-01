package Com.Spacefinders.DTO.Response;

public class PropertyResponse {

    private Long propertyId;
    private String propertyName;
    private String propertyDescription;
    private Integer noOfRooms;
    private Integer noOfBathrooms;
    private Integer maxNoOfGuests;
    private Double pricePerDay;
    private String imageURL;
    private String city;
    private String state;
    private String country;
    private String propertyStatus;
    private Double propertyRate;
    private Integer propertyRatingCount;

    // Amenities
    private Boolean hasWifi;
    private Boolean hasParking;
    private Boolean hasPool;
    private Boolean hasAc;
    private Boolean hasHeater;
    private Boolean hasPetFriendly;

    // Constructors
    public PropertyResponse() {
    }

    // Getters and Setters
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

    public String getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(String propertyStatus) {
        this.propertyStatus = propertyStatus;
    }

    public Double getPropertyRate() {
        return propertyRate;
    }

    public void setPropertyRate(Double propertyRate) {
        this.propertyRate = propertyRate;
    }

    public Integer getPropertyRatingCount() {
        return propertyRatingCount;
    }

    public void setPropertyRatingCount(Integer propertyRatingCount) {
        this.propertyRatingCount = propertyRatingCount;
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
}
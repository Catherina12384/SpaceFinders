package Com.Spacefinders.Entity;

import Com.Spacefinders.Enums.PropertyStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "property")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    @Column(nullable = false)
    private String propertyName;

    @Column(nullable = false, length = 1000)
    private String propertyDescription;

    @Column(nullable = false)
    private Integer noOfRooms;

    @Column(nullable = false)
    private Integer noOfBathrooms;

    @Column(nullable = false)
    private Integer maxNoOfGuests;

    @Column(nullable = false)
    private Double pricePerDay;

    @Column(nullable = false)
    private Long userId;

    private String imageURL;

    @Column(nullable = false)
    private Long addressId;

    @Column(nullable = false)
    private Boolean hasWifi;

    @Column(nullable = false)
    private Boolean hasParking;

    @Column(nullable = false)
    private Boolean hasPool;

    @Column(nullable = false)
    private Boolean hasAc;

    @Column(nullable = false)
    private Boolean hasHeater;

    @Column(nullable = false)
    private Boolean hasPetFriendly;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus propertyStatus;

    @Column(nullable = false)
    private Double propertyRate;

    @Column(nullable = false)
    private Integer propertyRatingCount;

    // Constructors
    public Property() {
    }

    public Property(Long propertyId, String propertyName, String propertyDescription,
                    Integer noOfRooms, Integer noOfBathrooms, Integer maxNoOfGuests,
                    Double pricePerDay, Long userId, String imageURL, Long addressId,
                    Boolean hasWifi, Boolean hasParking, Boolean hasPool, Boolean hasAc,
                    Boolean hasHeater, Boolean hasPetFriendly, PropertyStatus propertyStatus,
                    Double propertyRate, Integer propertyRatingCount) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.propertyDescription = propertyDescription;
        this.noOfRooms = noOfRooms;
        this.noOfBathrooms = noOfBathrooms;
        this.maxNoOfGuests = maxNoOfGuests;
        this.pricePerDay = pricePerDay;
        this.userId = userId;
        this.imageURL = imageURL;
        this.addressId = addressId;
        this.hasWifi = hasWifi;
        this.hasParking = hasParking;
        this.hasPool = hasPool;
        this.hasAc = hasAc;
        this.hasHeater = hasHeater;
        this.hasPetFriendly = hasPetFriendly;
        this.propertyStatus = propertyStatus;
        this.propertyRate = propertyRate;
        this.propertyRatingCount = propertyRatingCount;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
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

    public PropertyStatus getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(PropertyStatus propertyStatus) {
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
}
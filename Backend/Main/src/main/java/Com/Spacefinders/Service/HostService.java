//Audit log added

package Com.Spacefinders.Service;

import Com.Spacefinders.DTO.Request.ComplaintRequest;
import Com.Spacefinders.DTO.Request.PropertyRequest;
import Com.Spacefinders.DTO.Request.PropertyUpdateRequest;
import Com.Spacefinders.DTO.Response.BookingResponse;
import Com.Spacefinders.DTO.Response.ComplaintResponse;
import Com.Spacefinders.DTO.Response.PropertyDetailResponse;
import Com.Spacefinders.DTO.Response.PropertyResponse;
import Com.Spacefinders.Entity.*;
import Com.Spacefinders.Enums.*;
import Com.Spacefinders.Exception.*;
import Com.Spacefinders.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HostService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    // FIXED: Added @Transactional for atomicity auditLog added
    public PropertyResponse addProperty(PropertyRequest request) {
        // Validate user exists
        User host = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Host not found"));

        // Check if user is a HOST
        if (host.getUserRole() != UserRole.HOST) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Only hosts can add properties, Username: " + host.getUsername());
            throw new UnauthorizedException("Only hosts can add properties");
        }

        // Create address
        Address address = new Address();
        address.setBuildingNo(request.getBuildingNo());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        Address savedAddress = addressRepository.save(address);

        // Create property
        Property property = new Property();
        property.setPropertyName(request.getPropertyName());
        property.setPropertyDescription(request.getPropertyDescription());
        property.setNoOfRooms(request.getNoOfRooms());
        property.setNoOfBathrooms(request.getNoOfBathrooms());
        property.setMaxNoOfGuests(request.getMaxNoOfGuests());
        property.setPricePerDay(request.getPricePerDay());
        property.setUserId(request.getUserId());
        property.setImageURL(request.getImageURL());
        property.setAddressId(savedAddress.getAddressId());
        property.setHasWifi(request.getHasWifi());
        property.setHasParking(request.getHasParking());
        property.setHasPool(request.getHasPool());
        property.setHasAc(request.getHasAc());
        property.setHasHeater(request.getHasHeater());
        property.setHasPetFriendly(request.getHasPetFriendly());
        property.setPropertyStatus(PropertyStatus.AVAILABLE);
        property.setPropertyRate(0.0);
        property.setPropertyRatingCount(0);

        Property savedProperty = propertyRepository.save(property);

        return convertToPropertyResponse(savedProperty);
    }

    // FIXED: Added @Transactional auditLog added
    @Transactional
    public PropertyResponse updateProperty(PropertyUpdateRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));

        // Update property details
        property.setPropertyName(request.getPropertyName());
        property.setPropertyDescription(request.getPropertyDescription());
        property.setNoOfRooms(request.getNoOfRooms());
        property.setNoOfBathrooms(request.getNoOfBathrooms());
        property.setMaxNoOfGuests(request.getMaxNoOfGuests());
        property.setPricePerDay(request.getPricePerDay());
        property.setImageURL(request.getImageURL());
        property.setHasWifi(request.getHasWifi());
        property.setHasParking(request.getHasParking());
        property.setHasPool(request.getHasPool());
        property.setHasAc(request.getHasAc());
        property.setHasHeater(request.getHasHeater());
        property.setHasPetFriendly(request.getHasPetFriendly());

        // FIXED: Validate enum before setting
        try {
            property.setPropertyStatus(PropertyStatus.valueOf(request.getPropertyStatus()));
        } catch (IllegalArgumentException e) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Invalid property status, PropertyId: " + request.getPropertyId() +
                    " PropertyStatus: " + request.getPropertyStatus());
            throw new InvalidOperationException("Invalid property status: " + request.getPropertyStatus());
        }

        // FIXED: Use custom exception instead of generic RuntimeException
        Address address = addressRepository.findById(property.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found for property"));

        address.setBuildingNo(request.getBuildingNo());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);
        auditService.createAuditLog(ActionType.UPDATE, "Property Updated PropertyId: " + request.getPropertyId());

        Property updatedProperty = propertyRepository.save(property);

        return convertToPropertyResponse(updatedProperty);
    }

    // FIXED: Added @Transactional(readOnly = true) auditLog not needed
    @Transactional(readOnly = true)
    public List<PropertyResponse> viewAllProperty(int userId) {
        List<Property> properties = propertyRepository.findByUserId(userId);

        List<PropertyResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            // Filter out DELETED properties
            if (property.getPropertyStatus() != PropertyStatus.DELETED) {
                propertyResponses.add(convertToPropertyResponse(property));
            }
        }

        return propertyResponses;
    }

    @Transactional(readOnly = true)
    public PropertyDetailResponse viewPropertyById(int propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));

        // FIXED: Use custom exception
        Address address = addressRepository.findById(property.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found for property"));

        User host = userRepository.findById(property.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Host not found"));

        return convertToPropertyDetailResponse(property, address, host);
    }

    @Transactional
    public void setToUnavailableProperty(int propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));


        // Check if property status is AVAILABLE
        if (property.getPropertyStatus() != PropertyStatus.AVAILABLE) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to pull down a property but Only available " +
                    "properties " +
                    "can be " +
                    "made unavailable, PropertyId: " + propertyId);

            throw new InvalidOperationException("Only available properties can be pulled down");
        }

        // Check for active bookings
        List<Booking> activeBookings = bookingRepository.findByPropertyId(propertyId);
        for (Booking booking : activeBookings) {
            if (booking.getIsBookingStatus() == BookingStatus.BOOKED) {
                auditService.createAuditLog(ActionType.ERROR, "Tried to pull down a property but cannot " +
                        "property" +
                        " with " +
                        "active bookings, PropertyId: " + propertyId);
                throw new InvalidOperationException("Cannot pull down a property with active bookings");
            }
        }


        property.setPropertyStatus(PropertyStatus.UNAVAILABLE);
        propertyRepository.save(property);
        auditService.createAuditLog(ActionType.UPDATE, "property Unavailable Done, PropertyId: " + propertyId);
    }

    @Transactional
    public void deleteProperty(int propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));


        // Check if property status is AVAILABLE
        if (property.getPropertyStatus() != PropertyStatus.AVAILABLE) {
            auditService.createAuditLog(ActionType.ERROR, "Tried to delete property but Only available properties " +
                    "can be " +
                    "deleted, PropertyId: " + propertyId);

            throw new InvalidOperationException("Only available properties can be deleted");
        }

        // Check for active bookings
        List<Booking> activeBookings = bookingRepository.findByPropertyId(propertyId);
        for (Booking booking : activeBookings) {
            if (booking.getIsBookingStatus() == BookingStatus.BOOKED) {
                auditService.createAuditLog(ActionType.ERROR, "Tried to delete property but cannot delete property " +
                        "with " +
                        "active bookings, PropertyId: " + propertyId);
                throw new InvalidOperationException("Cannot delete property with active bookings");
            }
        }

        // Soft delete
        property.setPropertyStatus(PropertyStatus.DELETED);
        propertyRepository.save(property);
        auditService.createAuditLog(ActionType.DELETE, "property Deletion Done, PropertyId: " + propertyId);
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> viewDeleteProperty(int userId) {
        List<Property> properties = propertyRepository.findByUserIdAndPropertyStatus(
                userId, PropertyStatus.DELETED
        );

        List<PropertyResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            propertyResponses.add(convertToPropertyResponse(property));
        }

        return propertyResponses;
    }

    @Transactional(readOnly = true)// views all bookings history & current
    public List<BookingResponse> viewBookings(int userId) {
        List<Booking> bookings = bookingRepository.findBookingsByHostId(userId);

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(convertToBookingResponse(booking));
        }

        return bookingResponses;
    }

    @Transactional
    public ComplaintResponse addComplaint(ComplaintRequest request) {
        // Check if booking exists
        if (request.getBookingId() != null) {
            bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        }

        //Check if checkout date is not more than 7 days back
        Booking booking = bookingRepository.findById(request.getBookingId()).get();
        LocalDate currentDate = LocalDate.now();
        long dateDifference = ChronoUnit.DAYS.between(currentDate,booking.getCheckoutDate());
        if(dateDifference >= 7){
            throw new InvalidOperationException("Complaints can be registered only within 7 days of Check-Out. Your " +
                    "checkout date was " + booking.getCheckoutDate());

        }

        // Create complaint
        Complaint complaint = new Complaint();
        complaint.setUserId(request.getUserId());
        complaint.setBookingId(request.getBookingId());
        complaint.setComplaintDescription(request.getComplaintDescription());

        // FIXED: Validate enum before setting
        try {
            complaint.setComplaintType(ComplaintType.valueOf(request.getComplaintType()));
        } catch (IllegalArgumentException e) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to add complaint but invalid complaint type userId: " + complaint.getUserId() +  " complaint type: " + complaint.getComplaintType());
            throw new InvalidOperationException("Invalid complaint type: " + request.getComplaintType());
        }

        complaint.setComplaintStatus(ComplaintStatus.PENDING);
        complaint.setComplaintDate(LocalDateTime.now());

        Complaint savedComplaint = complaintRepository.save(complaint);
        auditService.createAuditLog(ActionType.CREATE, "complaint registered, userId: " + complaint.getUserId() +  " " +
                "complaintId: " + complaint.getComplaintId());
        return convertToComplaintResponse(savedComplaint);
    }

    // Helper methods remain the same but with better null handling
    private PropertyResponse convertToPropertyResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        response.setPropertyId(property.getPropertyId());
        response.setPropertyName(property.getPropertyName());
        response.setPropertyDescription(property.getPropertyDescription());
        response.setNoOfRooms(property.getNoOfRooms());
        response.setNoOfBathrooms(property.getNoOfBathrooms());
        response.setMaxNoOfGuests(property.getMaxNoOfGuests());
        response.setPricePerDay(property.getPricePerDay());
        response.setImageURL(property.getImageURL());
        response.setPropertyStatus(property.getPropertyStatus().toString());
        response.setPropertyRate(property.getPropertyRate());
        response.setPropertyRatingCount(property.getPropertyRatingCount());
        response.setHasWifi(property.getHasWifi());
        response.setHasParking(property.getHasParking());
        response.setHasPool(property.getHasPool());
        response.setHasAc(property.getHasAc());
        response.setHasHeater(property.getHasHeater());
        response.setHasPetFriendly(property.getHasPetFriendly());

        // FIXED: Better null handling with custom exception
        Address address = addressRepository.findById(property.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found for property"));

        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());

        return response;
    }

    private PropertyDetailResponse convertToPropertyDetailResponse(Property property, Address address, User host) {
        PropertyDetailResponse response = new PropertyDetailResponse();
        response.setPropertyId(property.getPropertyId());
        response.setPropertyName(property.getPropertyName());
        response.setPropertyDescription(property.getPropertyDescription());
        response.setNoOfRooms(property.getNoOfRooms());
        response.setNoOfBathrooms(property.getNoOfBathrooms());
        response.setMaxNoOfGuests(property.getMaxNoOfGuests());
        response.setPricePerDay(property.getPricePerDay());
        response.setImageURL(property.getImageURL());
        response.setPropertyStatus(property.getPropertyStatus().toString());
        response.setPropertyRate(property.getPropertyRate());
        response.setPropertyRatingCount(property.getPropertyRatingCount());
        response.setHasWifi(property.getHasWifi());
        response.setHasParking(property.getHasParking());
        response.setHasPool(property.getHasPool());
        response.setHasAc(property.getHasAc());
        response.setHasHeater(property.getHasHeater());
        response.setHasPetFriendly(property.getHasPetFriendly());

        // Address details
        response.setBuildingNo(address.getBuildingNo());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPostalCode(address.getPostalCode());

        // Host details
        response.setHostId(host.getUserId());
        response.setHostName(host.getUsername());
        response.setHostPhone(host.getUserPhone());

        return response;
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setPropertyId(booking.getPropertyId());
        response.setUserId(booking.getUserId());
        response.setCheckinDate(booking.getCheckinDate());
        response.setCheckoutDate(booking.getCheckoutDate());
        response.setIsPaymentStatus(booking.getIsPaymentStatus());
        response.setIsBookingStatus(booking.getIsBookingStatus().toString());
        response.setHasExtraCot(booking.getHasExtraCot());
        response.setHasDeepClean(booking.getHasDeepClean());

        // Get property details - FIXED: Better null handling
        Property property = propertyRepository.findById(booking.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException("Property not found for booking"));

        response.setPropertyName(property.getPropertyName());
        response.setPropertyImage(property.getImageURL());

        Address address = addressRepository.findById(property.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found for property"));
        response.setCity(address.getCity());

        // Get user details
        User user = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found for booking"));
        response.setUsername(user.getUsername());

        return response;
    }

    private ComplaintResponse convertToComplaintResponse(Complaint complaint) {
        ComplaintResponse response = new ComplaintResponse();
        response.setComplaintId(complaint.getComplaintId());
        response.setUserId(complaint.getUserId());
        response.setBookingId(complaint.getBookingId());
        response.setComplaintDescription(complaint.getComplaintDescription());
        response.setComplaintStatus(complaint.getComplaintStatus().toString());
        response.setComplaintType(complaint.getComplaintType().toString());
        response.setComplaintDate(complaint.getComplaintDate());

        // Get username
        User user = userRepository.findById(complaint.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found for complaint"));
        response.setUsername(user.getUsername());

        return response;
    }


}
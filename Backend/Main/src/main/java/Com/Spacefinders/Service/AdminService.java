//auditLog added

package Com.Spacefinders.Service;

import Com.Spacefinders.DTO.Request.BlockUserRequest;
import Com.Spacefinders.DTO.Request.RatingRequest;
import Com.Spacefinders.DTO.Response.BookingResponse;
import Com.Spacefinders.DTO.Response.ComplaintResponse;
import Com.Spacefinders.DTO.Response.PropertyResponse;
import Com.Spacefinders.DTO.Response.UserResponse;
import Com.Spacefinders.Entity.*;
import Com.Spacefinders.Enums.*;
import Com.Spacefinders.Exception.*;
import Com.Spacefinders.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private AuditService auditService;

    // Block User auditLog added
    public UserResponse blockUser(BlockUserRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Prevent blocking admin users
        if (user.getUserRole() == UserRole.ADMIN) {
            auditService.createAuditLog(ActionType.ERROR, "Cannot block admin users, UserName :" + user.getUsername());
            throw new UnauthorizedException("Cannot block admin users");
        }

        // Check if user is already in the requested status
        UserStatus requestedStatus = UserStatus.valueOf(request.getUserStatus());
        if (user.getUserStatus() == requestedStatus) {
            auditService.createAuditLog(ActionType.ERROR, "User is already blocked UserName :" + user.getUsername());
            throw new InvalidOperationException("User is already " + request.getUserStatus().toLowerCase());
        }

        // Update user status
        user.setUserStatus(requestedStatus);
        User updatedUser = userRepository.save(user);
        auditService.createAuditLog(ActionType.UPDATE, "User Blocked, UserName :" + user.getUsername());

        return convertToUserResponse(updatedUser);
    }

    // Unblock User auditLog added
    public UserResponse unblockUser(BlockUserRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update user status to ACTIVE
        user.setUserStatus(UserStatus.ACTIVE);
        User updatedUser = userRepository.save(user);
        auditService.createAuditLog(ActionType.UPDATE, "User Unblocked, UserName :" + user.getUsername());

        return convertToUserResponse(updatedUser);
    }

    // View All Users auditLog not needed
    public List<UserResponse> viewAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(convertToUserResponse(user));
        }

        return userResponses;
    }

    // View Users Based on Status auditLog not needed
    public List<UserResponse> viewUserBasedOnStatus(String status) {
        UserStatus userStatus = UserStatus.valueOf(status);
        List<User> users = userRepository.findByUserStatus(userStatus);

        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(convertToUserResponse(user));
        }

        return userResponses;
    }

    // View All Properties auditLog not needed
    public List<PropertyResponse> viewAllProperties() {
        List<Property> properties = propertyRepository.findAll();

        List<PropertyResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            propertyResponses.add(convertToPropertyResponse(property));
        }

        return propertyResponses;
    }

    // Delete Property (Admin Override - Soft Delete) auditLog added
    public void deleteProperty(int propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));

        Optional<Booking> booking = bookingRepository.findById(propertyId);

        if(booking.isPresent()) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Tried to delete but Property has active booking Property Name: " + property.getPropertyName());
            throw new InvalidOperationException("Property has an active booking");
        }

        // Check if property is already deleted
        if (property.getPropertyStatus() == PropertyStatus.DELETED) {
            auditService.createAuditLog(ActionType.ERROR, "Property is already deleted, propertyId :" + propertyId);
            throw new InvalidOperationException("Property is already deleted");
        }

        // Soft delete (admin can delete regardless of bookings)
        property.setPropertyStatus(PropertyStatus.DELETED);
        propertyRepository.save(property);
        auditService.createAuditLog(ActionType.DELETE, "Property deleted, propertyId :" + propertyId);

    }

    // View All Bookings auditLog not needed
    public List<BookingResponse> viewAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(convertToBookingResponse(booking));
        }

        return bookingResponses;
    }

    // Close Booking and Add Rating auditLog added
    public BookingResponse closeBookingAndRating(RatingRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));


        // Check if booking is available
        if (booking.getIsBookingStatus() == BookingStatus.CLOSED) {
            auditService.createAuditLog(ActionType.ERROR,
                    "Cannot close an available status booking, BookingId :" + request.getBookingId());
            throw new InvalidOperationException("Cannot close an available status booking");
        }

        // Update booking status to COMPLETED
        booking.setIsBookingStatus(BookingStatus.CLOSED);
        bookingRepository.save(booking);
        auditService.createAuditLog(ActionType.UPDATE, "Booking closed, BookingId :" + request.getBookingId());


        // Update property rating
        Property property = propertyRepository.findById(booking.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));

        // Calculate new average rating
        Double currentRate = property.getPropertyRate();
        Integer currentCount = property.getPropertyRatingCount();

        Double newRate = ((currentRate * currentCount) + request.getRating()) / (currentCount + 1);

        property.setPropertyRate(newRate);
        property.setPropertyRatingCount(currentCount + 1);
        propertyRepository.save(property);
        auditService.createAuditLog(ActionType.UPDATE, "Rating updated, PropertyId :" + property.getPropertyId());

        return convertToBookingResponse(booking);
    }

    // View All Complaints auditLog not needed
    public List<ComplaintResponse> viewAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();

        List<ComplaintResponse> complaintResponses = new ArrayList<>();
        for (Complaint complaint : complaints) {
            complaintResponses.add(convertToComplaintResponse(complaint));
        }

        return complaintResponses;
    }

    // Update Complaint Status auditLog added
    public ComplaintResponse updateComplaintStatus(int complaintId, String status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found"));

        ComplaintStatus complaintStatus = ComplaintStatus.valueOf(status);
        complaint.setComplaintStatus(complaintStatus);

        Complaint updatedComplaint = complaintRepository.save(complaint);
        auditService.createAuditLog(ActionType.UPDATE, "Complaint Status Updated, ComplaintId :" + complaintId);

        return convertToComplaintResponse(updatedComplaint);
    }

    // Close Complaint auditLog added
    public ComplaintResponse closeComplaint(int complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found"));

        complaint.setComplaintStatus(ComplaintStatus.CLOSED);
        Complaint updatedComplaint = complaintRepository.save(complaint);
        auditService.createAuditLog(ActionType.UPDATE, "Complaint Closed, ComplaintId :" + complaintId);
        return convertToComplaintResponse(updatedComplaint);
    }

    // Helper method to convert User to UserResponse
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setUserMail(user.getUserMail());
        response.setUserPhone(user.getUserPhone());
        response.setUserAddress(user.getUserAddress());
        response.setUserRole(user.getUserRole().toString());
        response.setUserStatus(user.getUserStatus().toString());
        return response;
    }

    // Helper method to convert Property to PropertyResponse
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

        // Get address details
        Address address = addressRepository.findById(property.getAddressId()).orElse(null);
        if (address != null) {
            response.setCity(address.getCity());
            response.setState(address.getState());
            response.setCountry(address.getCountry());
        }

        return response;
    }

    // Helper method to convert Booking to BookingResponse
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

        // Get property details
        Property property = propertyRepository.findById(booking.getPropertyId()).orElse(null);
        if (property != null) {
            response.setPropertyName(property.getPropertyName());
            response.setPropertyImage(property.getImageURL());

            Address address = addressRepository.findById(property.getAddressId()).orElse(null);
            if (address != null) {
                response.setCity(address.getCity());
            }
        }

        // Get user details
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null) {
            response.setUsername(user.getUsername());
        }

        return response;
    }

    // Helper method to convert Complaint to ComplaintResponse
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
        User user = userRepository.findById(complaint.getUserId()).orElse(null);
        if (user != null) {
            response.setUsername(user.getUsername());
        }

        return response;
    }

}
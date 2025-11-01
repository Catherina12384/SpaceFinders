package Com.Spacefinders.Service;

import Com.Spacefinders.DTO.Request.BookingRequest;
import Com.Spacefinders.DTO.Request.ComplaintRequest;
import Com.Spacefinders.DTO.Request.SearchPropertyRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {

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

    // Search Properties
    public List<PropertyResponse> searchByFields(SearchPropertyRequest request) {
        // Validate dates
        if (request.getCheckoutDate().isBefore(request.getCheckinDate())) {
            throw new IllegalArgumentException("Checkout date must be after checkin date");
        }

        if (request.getCheckinDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Checkin date cannot be in the past");
        }

        // Search properties based on location and guests
        List<Property> properties = propertyRepository.searchProperties(
                request.getCity(),
                request.getState(),
                request.getCountry(),
                PropertyStatus.AVAILABLE,
                request.getNoOfGuests()
        );

        // Filter out properties with conflicting bookings
        List<PropertyResponse> availableProperties = new ArrayList<>();

        for (Property property : properties) {
            List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                    property.getPropertyId(),
                    request.getCheckinDate(),
                    request.getCheckoutDate()
            );

            // If no conflicting bookings, add to available list
            if (conflictingBookings.isEmpty()) {
                availableProperties.add(convertToPropertyResponse(property));
            }
        }

        return availableProperties;
    }

    // View Clicked Property (Property Details)
    public PropertyDetailResponse viewClickedProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));

        Address address = addressRepository.findById(property.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        User host = userRepository.findById(property.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Host not found"));

        return convertToPropertyDetailResponse(property, address, host);
    }

    // Make Booking
    public BookingResponse makeBooking(BookingRequest request) {
        // Check property exists
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));

        // Check property is available
        if (property.getPropertyStatus() != PropertyStatus.AVAILABLE) {
            throw new InvalidOperationException("Property is not available for booking");
        }

        // Validate dates
        if (request.getCheckoutDate().isBefore(request.getCheckinDate())) {
            throw new IllegalArgumentException("Checkout date must be after checkin date");
        }

        if (request.getCheckinDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Checkin date cannot be in the past");
        }

        // Check for conflicting bookings
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                request.getPropertyId(),
                request.getCheckinDate(),
                request.getCheckoutDate()
        );

        if (!conflictingBookings.isEmpty()) {
            throw new InvalidOperationException("Property already booked for selected dates");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setPropertyId(request.getPropertyId());
        booking.setUserId(request.getUserId());
        booking.setCheckinDate(request.getCheckinDate());
        booking.setCheckoutDate(request.getCheckoutDate());
        booking.setHasExtraCot(request.getHasExtraCot());
        booking.setHasDeepClean(request.getHasDeepClean());
        booking.setIsPaymentStatus(true); // Assume payment done
        booking.setIsBookingStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        return convertToBookingResponse(savedBooking);
    }

    // View Bookings
    public List<BookingResponse> viewBooking(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(convertToBookingResponse(booking));
        }

        return bookingResponses;
    }

    // Cancel Booking
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        // Check if booking can be cancelled
        if (booking.getIsBookingStatus() == BookingStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot cancel completed booking");
        }

        if (booking.getIsBookingStatus() == BookingStatus.CANCELLED) {
            throw new InvalidOperationException("Booking is already cancelled");
        }

        // Cancel booking (soft delete)
        booking.setIsBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    // Add Complaint for Booking
    public ComplaintResponse addComplaintForBooking(ComplaintRequest request) {
        // Check if booking exists
        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        }

        // Create complaint
        Complaint complaint = new Complaint();
        complaint.setUserId(request.getUserId());
        complaint.setBookingId(request.getBookingId());
        complaint.setComplaintDescription(request.getComplaintDescription());
        complaint.setComplaintType(ComplaintType.valueOf(request.getComplaintType()));
        complaint.setComplaintStatus(ComplaintStatus.PENDING);
        complaint.setComplaintDate(LocalDateTime.now());

        Complaint savedComplaint = complaintRepository.save(complaint);

        return convertToComplaintResponse(savedComplaint);
    }

    // View Complaints by User
    public List<ComplaintResponse> viewComplaints(Long userId) {
        List<Complaint> complaints = complaintRepository.findByUserId(userId);

        List<ComplaintResponse> complaintResponses = new ArrayList<>();
        for (Complaint complaint : complaints) {
            complaintResponses.add(convertToComplaintResponse(complaint));
        }

        return complaintResponses;
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

    // Helper method to convert to PropertyDetailResponse
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
package Com.Spacefinders.Service;
import Com.Spacefinders.DTO.Response.BookingResponse;
import Com.Spacefinders.Entity.Address;
import Com.Spacefinders.Entity.Booking;
import Com.Spacefinders.Entity.Property;
import Com.Spacefinders.Entity.User;
import Com.Spacefinders.Exception.BookingNotFoundException;
import Com.Spacefinders.Repository.AddressRepository;
import Com.Spacefinders.Repository.BookingRepository;
import Com.Spacefinders.Repository.PropertyRepository;
import Com.Spacefinders.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    // Get Booking by ID
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        return convertToBookingResponse(booking);
    }

    // Get All Bookings
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(convertToBookingResponse(booking));
        }

        return bookingResponses;
    }

    // Get Bookings by User ID
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(convertToBookingResponse(booking));
        }

        return bookingResponses;
    }

    // Get Bookings by Property ID
    public List<BookingResponse> getBookingsByPropertyId(Long propertyId) {
        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId);

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(convertToBookingResponse(booking));
        }

        return bookingResponses;
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
}
package Com.Spacefinders.Controller;

import Com.Spacefinders.DTO.Request.BookingRequest;
import Com.Spacefinders.DTO.Request.ComplaintRequest;
import Com.Spacefinders.DTO.Request.SearchPropertyRequest;
import Com.Spacefinders.DTO.Response.*;
import Com.Spacefinders.Service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/client")
@CrossOrigin(origins = "http://localhost:4200")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // Search Properties by Fields
    @PostMapping("/searchByFields")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> searchByFields(
            @Valid @RequestBody SearchPropertyRequest request) {
        List<PropertyResponse> properties = clientService.searchByFields(request);
        ApiResponse<List<PropertyResponse>> response = new ApiResponse<>(
                true,
                "Properties retrieved successfully",
                properties
        );
        return ResponseEntity.ok(response);
    }

    // View Clicked Property (Property Details)
    @GetMapping("/viewClickedProperty/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyDetailResponse>> viewClickedProperty(@PathVariable Long propertyId) {
        PropertyDetailResponse property = clientService.viewClickedProperty(propertyId);
        ApiResponse<PropertyDetailResponse> response = new ApiResponse<>(
                true,
                "Property details retrieved successfully",
                property
        );
        return ResponseEntity.ok(response);
    }

    // Make Booking
    @PostMapping("/makeBooking")
    public ResponseEntity<ApiResponse<BookingResponse>> makeBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse booking = clientService.makeBooking(request);
        ApiResponse<BookingResponse> response = new ApiResponse<>(
                true,
                "Booking created successfully",
                booking
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // View Bookings
    @GetMapping("/viewBooking/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> viewBooking(@PathVariable Long userId) {
        List<BookingResponse> bookings = clientService.viewBooking(userId);
        ApiResponse<List<BookingResponse>> response = new ApiResponse<>(
                true,
                "Bookings retrieved successfully",
                bookings
        );
        return ResponseEntity.ok(response);
    }

    // Cancel Booking
    @DeleteMapping("/cancelBooking/{bookingId}")
    public ResponseEntity<ApiResponse<String>> cancelBooking(@PathVariable Long bookingId) {
        clientService.cancelBooking(bookingId);
        ApiResponse<String> response = new ApiResponse<>(true, "Booking cancelled successfully", null);
        return ResponseEntity.ok(response);
    }

    // Add Complaint for Booking
    @PostMapping("/addComplaintForBooking")
    public ResponseEntity<ApiResponse<ComplaintResponse>> addComplaintForBooking(
            @Valid @RequestBody ComplaintRequest request) {
        ComplaintResponse complaint = clientService.addComplaintForBooking(request);
        ApiResponse<ComplaintResponse> response = new ApiResponse<>(
                true,
                "Complaint submitted successfully",
                complaint
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
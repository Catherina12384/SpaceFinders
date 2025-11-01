package Com.Spacefinders.Controller;

import Com.Spacefinders.DTO.Request.ComplaintRequest;
import Com.Spacefinders.DTO.Request.PropertyRequest;
import Com.Spacefinders.DTO.Request.PropertyUpdateRequest;
import Com.Spacefinders.DTO.Response.*;
import Com.Spacefinders.Service.HostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/host")
@CrossOrigin(origins = "http://localhost:4200")
public class HostController {

    @Autowired
    private HostService hostService;

    // Add Property
    @PostMapping("/addProperty")
    public ResponseEntity<ApiResponse<PropertyResponse>> addProperty(@Valid @RequestBody PropertyRequest request) {
        PropertyResponse property = hostService.addProperty(request);
        ApiResponse<PropertyResponse> response = new ApiResponse<>(
                true,
                "Property added successfully",
                property
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update Property
    @PutMapping("/updateProperty")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateProperty(
            @Valid @RequestBody PropertyUpdateRequest request) {
        PropertyResponse property = hostService.updateProperty(request);
        ApiResponse<PropertyResponse> response = new ApiResponse<>(
                true,
                "Property updated successfully",
                property
        );
        return ResponseEntity.ok(response);
    }

    // View All Properties by Host
    @GetMapping("/viewAllProperty/{userId}")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> viewAllProperty(@PathVariable Long userId) {
        List<PropertyResponse> properties = hostService.viewAllProperty(userId);
        ApiResponse<List<PropertyResponse>> response = new ApiResponse<>(
                true,
                "Properties retrieved successfully",
                properties
        );
        return ResponseEntity.ok(response);
    }

    // View Property by ID
    @GetMapping("/viewPropertyById/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyDetailResponse>> viewPropertyById(@PathVariable Long propertyId) {
        PropertyDetailResponse property = hostService.viewPropertyById(propertyId);
        ApiResponse<PropertyDetailResponse> response = new ApiResponse<>(
                true,
                "Property details retrieved successfully",
                property
        );
        return ResponseEntity.ok(response);
    }

    // Delete Property
    @DeleteMapping("/deleteProperty/{propertyId}")
    public ResponseEntity<ApiResponse<String>> deleteProperty(@PathVariable Long propertyId) {
        hostService.deleteProperty(propertyId);
        ApiResponse<String> response = new ApiResponse<>(true, "Property deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    // View Deleted Properties
    @GetMapping("/viewDeleteProperty/{userId}")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> viewDeleteProperty(@PathVariable Long userId) {
        List<PropertyResponse> properties = hostService.viewDeleteProperty(userId);
        ApiResponse<List<PropertyResponse>> response = new ApiResponse<>(
                true,
                "Deleted properties retrieved successfully",
                properties
        );
        return ResponseEntity.ok(response);
    }

    // View Bookings for Host's Properties
    @GetMapping("/viewBookings/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> viewBookings(@PathVariable Long userId) {
        List<BookingResponse> bookings = hostService.viewBookings(userId);
        ApiResponse<List<BookingResponse>> response = new ApiResponse<>(
                true,
                "Bookings retrieved successfully",
                bookings
        );
        return ResponseEntity.ok(response);
    }

    // Add Complaint
    @PostMapping("/addComplaint")
    public ResponseEntity<ApiResponse<ComplaintResponse>> addComplaint(@Valid @RequestBody ComplaintRequest request) {
        ComplaintResponse complaint = hostService.addComplaint(request);
        ApiResponse<ComplaintResponse> response = new ApiResponse<>(
                true,
                "Complaint submitted successfully",
                complaint
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
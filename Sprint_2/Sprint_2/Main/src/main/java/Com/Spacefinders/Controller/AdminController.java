package Com.Spacefinders.Controller;

import Com.Spacefinders.DTO.Request.BlockUserRequest;
import Com.Spacefinders.DTO.Request.RatingRequest;
import Com.Spacefinders.DTO.Response.*;
import Com.Spacefinders.Service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Block User
    @PutMapping("/blockUser")
    public ResponseEntity<ApiResponse<UserResponse>> blockUser(@Valid @RequestBody BlockUserRequest request) {
        UserResponse user = adminService.blockUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "User status updated successfully",
                user
        );
        return ResponseEntity.ok(response);
    }

    // Unblock User
    @PutMapping("/unblockUser")
    public ResponseEntity<ApiResponse<UserResponse>> unblockUser(@Valid @RequestBody BlockUserRequest request) {
        UserResponse user = adminService.unblockUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "User unblocked successfully",
                user
        );
        return ResponseEntity.ok(response);
    }

    // View All Users
    @GetMapping("/viewAllUsers")
    public ResponseEntity<ApiResponse<List<UserResponse>>> viewAllUsers() {
        List<UserResponse> users = adminService.viewAllUsers();
        ApiResponse<List<UserResponse>> response = new ApiResponse<>(
                true,
                "Users retrieved successfully",
                users
        );
        return ResponseEntity.ok(response);
    }

    // View Users Based on Status
    @GetMapping("/viewUserBasedOnStatus")
    public ResponseEntity<ApiResponse<List<UserResponse>>> viewUserBasedOnStatus(@RequestParam String status) {
        List<UserResponse> users = adminService.viewUserBasedOnStatus(status);
        ApiResponse<List<UserResponse>> response = new ApiResponse<>(
                true,
                "Users retrieved successfully",
                users
        );
        return ResponseEntity.ok(response);
    }

    // View All Properties
    @GetMapping("/viewAllProperties")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> viewAllProperties() {
        List<PropertyResponse> properties = adminService.viewAllProperties();
        ApiResponse<List<PropertyResponse>> response = new ApiResponse<>(
                true,
                "Properties retrieved successfully",
                properties
        );
        return ResponseEntity.ok(response);
    }

    // Delete Property (Admin Override)
    @DeleteMapping("/deleteProperty/{propertyId}")
    public ResponseEntity<ApiResponse<String>> deleteProperty(@PathVariable Long propertyId) {
        adminService.deleteProperty(propertyId);
        ApiResponse<String> response = new ApiResponse<>(true, "Property deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    // View All Bookings
    @GetMapping("/viewAllBookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> viewAllBookings() {
        List<BookingResponse> bookings = adminService.viewAllBookings();
        ApiResponse<List<BookingResponse>> response = new ApiResponse<>(
                true,
                "Bookings retrieved successfully",
                bookings
        );
        return ResponseEntity.ok(response);
    }

    // Close Booking and Add Rating
    @PutMapping("/closeBookingAndRating")
    public ResponseEntity<ApiResponse<BookingResponse>> closeBookingAndRating(
            @Valid @RequestBody RatingRequest request) {
        BookingResponse booking = adminService.closeBookingAndRating(request);
        ApiResponse<BookingResponse> response = new ApiResponse<>(
                true,
                "Booking closed and rating added successfully",
                booking
        );
        return ResponseEntity.ok(response);
    }

    // View All Complaints
    @GetMapping("/viewAllComplaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> viewAllComplaints() {
        List<ComplaintResponse> complaints = adminService.viewAllComplaints();
        ApiResponse<List<ComplaintResponse>> response = new ApiResponse<>(
                true,
                "Complaints retrieved successfully",
                complaints
        );
        return ResponseEntity.ok(response);
    }

    // Update Complaint Status
    @PutMapping("/updateComplaintStatus/{complaintId}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestParam String status) {
        ComplaintResponse complaint = adminService.updateComplaintStatus(complaintId, status);
        ApiResponse<ComplaintResponse> response = new ApiResponse<>(
                true,
                "Complaint status updated successfully",
                complaint
        );
        return ResponseEntity.ok(response);
    }

    // Close Complaint
    @PutMapping("/closeComplaint/{complaintId}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> closeComplaint(@PathVariable Long complaintId) {
        ComplaintResponse complaint = adminService.closeComplaint(complaintId);
        ApiResponse<ComplaintResponse> response = new ApiResponse<>(
                true,
                "Complaint closed successfully",
                complaint
        );
        return ResponseEntity.ok(response);
    }
}

//public ResponseEntity<List<Complaint>> getComplaintsByUserId(int userId) {
//    List<Complaint> complaints = cp.findByUserIdAndStatusNotClosed(userId, ComplaintsEnum.Status.closed);
//
//    if (complaints.isEmpty()) {
//        throw new DataNotFoundException("No open complaints found for user id: " + userId);
//    }
//
//    return ResponseEntity.ok(complaints);
//}
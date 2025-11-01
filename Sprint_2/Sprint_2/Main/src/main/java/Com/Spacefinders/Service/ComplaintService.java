package Com.Spacefinders.Service;

import Com.Spacefinders.DTO.Response.ComplaintResponse;
import Com.Spacefinders.Entity.Complaint;
import Com.Spacefinders.Entity.User;
import Com.Spacefinders.Exception.ComplaintNotFoundException;
import Com.Spacefinders.Repository.ComplaintRepository;
import Com.Spacefinders.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    // Get Complaint by ID
    public ComplaintResponse getComplaintById(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found"));

        return convertToComplaintResponse(complaint);
    }

    // Get All Complaints
    public List<ComplaintResponse> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();

        List<ComplaintResponse> complaintResponses = new ArrayList<>();
        for (Complaint complaint : complaints) {
            complaintResponses.add(convertToComplaintResponse(complaint));
        }

        return complaintResponses;
    }

    // Get Complaints by User ID
    public List<ComplaintResponse> getComplaintsByUserId(Long userId) {
        List<Complaint> complaints = complaintRepository.findByUserId(userId);

        List<ComplaintResponse> complaintResponses = new ArrayList<>();
        for (Complaint complaint : complaints) {
            complaintResponses.add(convertToComplaintResponse(complaint));
        }

        return complaintResponses;
    }

    // Get Complaints by Booking ID
    public List<ComplaintResponse> getComplaintsByBookingId(Long bookingId) {
        List<Complaint> complaints = complaintRepository.findByBookingId(bookingId);

        List<ComplaintResponse> complaintResponses = new ArrayList<>();
        for (Complaint complaint : complaints) {
            complaintResponses.add(convertToComplaintResponse(complaint));
        }

        return complaintResponses;
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
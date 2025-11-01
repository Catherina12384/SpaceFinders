package Com.Spacefinders.Repository;

import Com.Spacefinders.Entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {

    List<Complaint> findByUserId(int userId);

    List<Complaint> findByBookingId(int bookingId);
}
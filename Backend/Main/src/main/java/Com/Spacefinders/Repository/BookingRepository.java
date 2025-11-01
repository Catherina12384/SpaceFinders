package Com.Spacefinders.Repository;

import Com.Spacefinders.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserId(int userId);

    List<Booking> findByPropertyId(int propertyId);

    @Query("SELECT b FROM Booking b WHERE b.propertyId IN " +
            "(SELECT p.propertyId FROM Property p WHERE p.userId = :userId)")
    List<Booking> findBookingsByHostId(@Param("userId") int userId);

    @Query("SELECT b FROM Booking b WHERE b.propertyId = :propertyId " +
            "AND b.isBookingStatus IN ('BOOKED') " +
            "AND ((b.checkinDate <= :checkoutDate AND b.checkoutDate >= :checkinDate))")
    List<Booking> findConflictingBookings(
            @Param("propertyId") int propertyId,
            @Param("checkinDate") LocalDate checkinDate,
            @Param("checkoutDate") LocalDate checkoutDate
    );
}
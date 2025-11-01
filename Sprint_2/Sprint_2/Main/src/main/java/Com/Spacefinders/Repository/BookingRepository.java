package Com.Spacefinders.Repository;

import Com.Spacefinders.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByPropertyId(Long propertyId);

    @Query("SELECT b FROM Booking b WHERE b.propertyId IN " +
            "(SELECT p.propertyId FROM Property p WHERE p.userId = :userId)")
    List<Booking> findBookingsByHostId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.propertyId = :propertyId " +
            "AND b.isBookingStatus IN ('CONFIRMED', 'PENDING') " +
            "AND ((b.checkinDate <= :checkoutDate AND b.checkoutDate >= :checkinDate))")
    List<Booking> findConflictingBookings(
            @Param("propertyId") Long propertyId,
            @Param("checkinDate") LocalDate checkinDate,
            @Param("checkoutDate") LocalDate checkoutDate
    );
}
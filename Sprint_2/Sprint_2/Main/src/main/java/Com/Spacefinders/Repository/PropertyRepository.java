package Com.Spacefinders.Repository;

import Com.Spacefinders.Entity.Property;
import Com.Spacefinders.Enums.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByUserId(Long userId);

    List<Property> findByUserIdAndPropertyStatus(Long userId, PropertyStatus propertyStatus);

    @Query("SELECT p FROM Property p JOIN Address a ON p.addressId = a.addressId " +
            "WHERE (:city IS NULL OR a.city = :city) " +
            "AND (:state IS NULL OR a.state = :state) " +
            "AND (:country IS NULL OR a.country = :country) " +
            "AND p.propertyStatus = :status " +
            "AND p.maxNoOfGuests >= :guests")
    List<Property> searchProperties(
            @Param("city") String city,
            @Param("state") String state,
            @Param("country") String country,
            @Param("status") PropertyStatus status,
            @Param("guests") Integer guests
    );
}
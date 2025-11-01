package Com.Spacefinders.Repository;

import Com.Spacefinders.Entity.User;
import Com.Spacefinders.Enums.UserRole;
import Com.Spacefinders.Enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsernameIgnoreCase(String username);

    User findByUserMail(String userMail);

    User findByUserPhone(String userPhone);

    List<User> findByUserStatus(UserStatus userStatus);

    @Query("SELECT u.userId FROM User u WHERE u.userRole = :role")
    List<Integer> findUserIdByUserRole(@Param("role") UserRole userRole);

}
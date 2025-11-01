package Com.Spacefinders.Repository;

import Com.Spacefinders.Entity.User;
import Com.Spacefinders.Enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByUserMail(String userMail);

    User findByUserPhone(String userPhone);

    List<User> findByUserStatus(UserStatus userStatus);
}
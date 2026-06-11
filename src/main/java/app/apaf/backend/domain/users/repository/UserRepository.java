package app.apaf.backend.domain.users.repository;

import app.apaf.backend.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> FindByEmail(String email);
}

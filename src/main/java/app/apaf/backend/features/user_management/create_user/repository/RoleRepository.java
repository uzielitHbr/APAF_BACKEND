package app.apaf.backend.features.user_management.create_user.repository;

import app.apaf.backend.domain.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCodeRole(String codeRole);
}

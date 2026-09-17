package sn.sdley.springbootstarter0.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.sdley.springbootstarter0.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);
}

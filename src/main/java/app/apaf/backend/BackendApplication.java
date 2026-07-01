package app.apaf.backend;


import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.Role;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import app.apaf.backend.features.user_management.create_user.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner initSuperAdmin(UserRepository userRepository,
	                                 RoleRepository roleRepository,
	                                 PasswordEncoder passwordEncoder) {
		return args -> {
			String adminEmail = "uzielalbertoar@gmail.com";

			if (userRepository.findByEmail(adminEmail).isEmpty()) {

				Role adminRole = roleRepository.findByCodeRole("ADMIN")
						.orElseThrow(() -> new RuntimeException("El rol ADMIN no existe en la base de datos"));

				User superAdmin = new User();
				superAdmin.setFullName("Uziel Alberto Abraham Rendon");
				superAdmin.setEmail(adminEmail);
				superAdmin.setPassword(passwordEncoder.encode("123456789"));
				superAdmin.setPhoneNumber("7771234567");
				superAdmin.setRole(adminRole);
				superAdmin.setStatus(UserStatus.ACTIVO);
				superAdmin.setAccountLocked(false);
				superAdmin.setFailedAttempts(0);

				userRepository.save(superAdmin);

				System.out.println("Administrador listo ");
			}
		};
	}

}

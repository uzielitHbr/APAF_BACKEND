package app.apaf.backend;

import app.apaf.backend.domain.enums.RoleUser;
import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
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
    CommandLineRunner initSuperAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			String correoAdmin = "uzielalbertoar@gmail.com";

			if (userRepository.findByEmail(correoAdmin).isEmpty()) {

				User superAdmin = new User();
				superAdmin.setFullName("Uziel Alberto Abraham Rendon");
				superAdmin.setEmail(correoAdmin);
				superAdmin.setPassword(passwordEncoder.encode("123456789"));

				superAdmin.setPhoneNumber("7772549830");
				superAdmin.setRole(RoleUser.ADMIN);
				superAdmin.setStatus(UserStatus.ACTIVO);

				userRepository.save(superAdmin);

				System.out.println("✅ Super Administrador creado con éxito en la base de datos.");
			} else {
				System.out.println("⚡ El Super Administrador ya existe. Omitiendo creación.");
			}
		};
	}

}

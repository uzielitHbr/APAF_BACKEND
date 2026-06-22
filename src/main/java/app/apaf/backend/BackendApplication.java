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


}

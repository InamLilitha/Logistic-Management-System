package za.ac.cput.logisticmanagementsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;

@Configuration
public class StaffProfileDataInitializer {

    @Bean
    CommandLineRunner seedDefaultAdmin(StaffProfileRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!repository.existsByUsername("admin")) {
                repository.save(new StaffProfile(
                        "admin",
                        "System Administrator",
                        passwordEncoder.encode("admin123"),
                        "ADMIN"
                ));
            }
        };
    }
}

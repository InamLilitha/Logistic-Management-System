package za.ac.cput.logisticmanagementsystem.repository;

import java.util.List;
import java.util.Optional;
import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
    Optional<StaffProfile> findByUsernameAndActiveTrue(String username);
    Optional<StaffProfile> findByEmailIgnoreCase(String email);
    List<StaffProfile> findAllByRoleOrderByNameAsc(String role);
    boolean existsByUsername(String username);
}

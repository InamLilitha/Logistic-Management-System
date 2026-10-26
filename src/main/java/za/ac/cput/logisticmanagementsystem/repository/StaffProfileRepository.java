package za.ac.cput.logisticmanagementsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
    Optional<StaffProfile> findByUsernameAndActiveTrue(String username);
    List<StaffProfile> findAllByRoleOrderByNameAsc(String role);
    boolean existsByUsername(String username);
}

package za.ac.cput.logisticmanagementsystem.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;

@Service
public class StaffProfileService {

    private final StaffProfileRepository repository;
    private final PasswordEncoder passwordEncoder;

    public StaffProfileService(StaffProfileRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public StaffProfile authenticate(String username, String password) {
        return repository.findByUsernameAndActiveTrue(username)
                .filter(profile -> passwordEncoder.matches(password, profile.getPasswordHash()))
                .orElse(null);
    }

    @Transactional
    public StaffProfile createStaff(String name, String username, String password) {
        String normalizedUsername = username.trim();
        if (repository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("That username is already in use.");
        }
        return repository.save(new StaffProfile(
                normalizedUsername,
                name.trim(),
                passwordEncoder.encode(password),
                "STAFF"
        ));
    }

    public List<StaffProfile> getStaffProfiles() {
        return repository.findAllByRoleOrderByNameAsc("STAFF");
    }

    @Transactional
    public StaffProfile setStaffAccess(Long id, boolean active) {
        StaffProfile profile = repository.findById(id)
                .filter(staff -> "STAFF".equals(staff.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("Staff profile was not found."));
        profile.setActive(active);
        return repository.save(profile);
    }

    @Transactional
    public void deleteStaff(Long id) {
        StaffProfile profile = repository.findById(id)
                .filter(staff -> "STAFF".equals(staff.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("Staff profile was not found."));
        repository.delete(profile);
    }
}

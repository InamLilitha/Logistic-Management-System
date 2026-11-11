package za.ac.cput.logisticmanagementsystem.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;

@Service
public class StaffProfileService {

    private final StaffProfileRepository repository;
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;

    public StaffProfileService(
            StaffProfileRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    public StaffProfile authenticate(String username, String password) {
        return repository.findByUsernameAndActiveTrue(username)
                .filter(profile ->
                        passwordEncoder.matches(
                                password,
                                profile.getPasswordHash()))
                .orElse(null);
    }

    @Transactional
    public StaffProfile createStaff(
            String name,
            String username,
            String email,
            String password) {

        validateName(name);
        validateUsername(username);
        validateEmail(email);

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        String normalizedName = name.trim();
        String normalizedUsername = username.trim();
        String normalizedEmail = email.trim().toLowerCase();

        if (repository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException(
                    "That username is already in use.");
        }

        if (repository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "That email address is already in use.");
        }

        StaffProfile profile = new StaffProfile(
                normalizedUsername,
                normalizedEmail,
                normalizedName,
                passwordEncoder.encode(password),
                "STAFF"
        );

        return repository.save(profile);
    }

    public List<StaffProfile> getStaffProfiles() {
        return repository.findAllByRoleOrderByNameAsc("STAFF");
    }

    @Transactional
    public StaffProfile updateStaff(
            Long id,
            String name,
            String username,
            String email) {

        validateName(name);
        validateUsername(username);
        validateEmail(email);

        StaffProfile existing = repository.findById(id)
                .filter(staff -> "STAFF".equals(staff.getRole()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Staff profile was not found."));

        String normalizedName = name.trim();
        String normalizedUsername = username.trim();
        String normalizedEmail = email.trim().toLowerCase();

        if (!existing.getUsername().equals(normalizedUsername)
                && repository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException(
                    "That username is already in use.");
        }

        if (!existing.getEmail().equalsIgnoreCase(normalizedEmail)
                && repository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "That email address is already in use.");
        }

        entityManager.createQuery("""
            UPDATE StaffProfile s
            SET s.name = :name,
                s.username = :username,
                s.email = :email
            WHERE s.id = :id
            """)
                .setParameter("name", normalizedName)
                .setParameter("username", normalizedUsername)
                .setParameter("email", normalizedEmail)
                .setParameter("id", id)
                .executeUpdate();

        entityManager.clear();

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Staff profile was not found."));
    }

    @Transactional
    public StaffProfile setStaffAccess(Long id, boolean active) {
        StaffProfile profile = repository.findById(id)
                .filter(staff -> "STAFF".equals(staff.getRole()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Staff profile was not found."));

        profile.setActive(active);
        return repository.save(profile);
    }

    @Transactional
    public void deleteStaff(Long id) {
        StaffProfile profile = repository.findById(id)
                .filter(staff -> "STAFF".equals(staff.getRole()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Staff profile was not found."));

        repository.delete(profile);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email address is required.");
        }

        if (!email.trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Please enter a valid email address.");
        }
    }
}


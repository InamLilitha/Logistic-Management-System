package za.ac.cput.logisticmanagementsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import za.ac.cput.logisticmanagementsystem.domain.PasswordResetRequest;
import za.ac.cput.logisticmanagementsystem.domain.PasswordResetStatus;

public interface PasswordResetRequestRepository
        extends JpaRepository<PasswordResetRequest, Long> {

    Optional<PasswordResetRequest> findByToken(UUID token);

    List<PasswordResetRequest> findByStatusOrderByCreatedAtAsc(
            PasswordResetStatus status);

    List<PasswordResetRequest> findByStaffProfile_Id(Long staffProfileId);

    // Retrieves all requests, newest first, for the admin history table.
    List<PasswordResetRequest> findAllByOrderByCreatedAtDesc();
}


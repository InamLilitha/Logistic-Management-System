package za.ac.cput.logisticmanagementsystem.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.cput.logisticmanagementsystem.domain.PasswordResetRequest;
import za.ac.cput.logisticmanagementsystem.domain.PasswordResetStatus;
import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.repository.PasswordResetRequestRepository;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;
import za.ac.cput.logisticmanagementsystem.service.IPasswordResetRequestService;
import za.ac.cput.logisticmanagementsystem.service.impl.EmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PasswordResetRequestService
        implements IPasswordResetRequestService {


    private final PasswordResetRequestRepository resetRepository;
    private final StaffProfileRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public PasswordResetRequestService(
            PasswordResetRequestRepository resetRepository,
            StaffProfileRepository staffRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.resetRepository = resetRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        staffRepository.findByEmailIgnoreCase(email.trim())
                .ifPresent(profile -> {
                    PasswordResetRequest request = new PasswordResetRequest.Builder()
                            .staffProfile(profile)
                            .token(UUID.randomUUID())
                            .status(PasswordResetStatus.PENDING)
                            .createdAt(LocalDateTime.now())
                            .expiresAt(LocalDateTime.now().plusHours(24))
                            .build();

                    resetRepository.save(request);
                });
                }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordResetRequest> getPendingRequests() {

        return resetRepository.findByStatusOrderByCreatedAtAsc(
                PasswordResetStatus.PENDING);
    }
    @Override
    @Transactional(readOnly = true)
    public List<PasswordResetRequest> getPasswordResetHistory() {
        return resetRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public void approveRequest(Long id) {

        PasswordResetRequest request = findRequest(id);

        if (request.getStatus() != PasswordResetStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending requests can be approved.");
        }

        if (isExpired(request)) {
            request.reject(LocalDateTime.now());

            throw new IllegalStateException(
                    "This password-reset request has expired.");
        }

        request.approve(LocalDateTime.now());

        StaffProfile profile = request.getStaffProfile();

        emailService.sendPasswordResetEmail(
                profile.getEmail(),
                profile.getName(),
                request.getToken().toString()
        );
    }

    @Override
    @Transactional
    public void rejectRequest(Long id) {

        PasswordResetRequest request = findRequest(id);

        if (request.getStatus() != PasswordResetStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending requests can be rejected.");
        }

        request.reject(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public String getRequestStatus(UUID token) {

        PasswordResetRequest request = findByToken(token);

        if (request.getStatus() == PasswordResetStatus.APPROVED
                && isExpired(request)) {
            return "EXPIRED";
        }

        return request.getStatus().name();
    }

    @Override
    @Transactional
    public void completePasswordReset(UUID token, String newPassword) {

        if (newPassword == null || newPassword.length() < 12) {
            throw new IllegalArgumentException(
                    "Password must contain at least 12 characters.");
        }

        PasswordResetRequest request = findByToken(token);

        if (request.getStatus() != PasswordResetStatus.APPROVED) {
            throw new IllegalStateException(
                    "This request has not been approved or is no longer valid.");
        }

        if (isExpired(request)) {
            throw new IllegalStateException(
                    "This password-reset token has expired.");
        }

        StaffProfile profile = request.getStaffProfile();

        if (!profile.isActive()) {
            throw new IllegalStateException(
                    "This staff account is inactive.");
        }

        profile.setPasswordHash(passwordEncoder.encode(newPassword));

        request.complete(LocalDateTime.now());
    }

    private PasswordResetRequest findRequest(Long id) {

        return resetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Password-reset request not found."));
    }

    private PasswordResetRequest findByToken(UUID token) {

        if (token == null) {
            throw new IllegalArgumentException("A valid token is required.");
        }

        return resetRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Password-reset request not found."));
    }

    private boolean isExpired(PasswordResetRequest request) {

        return !LocalDateTime.now().isBefore(request.getExpiresAt());
    }
}


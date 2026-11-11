package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.logisticmanagementsystem.domain.PasswordResetRequest;
import za.ac.cput.logisticmanagementsystem.service.IPasswordResetRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/password-reset")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class PasswordResetRequestController {

    private final IPasswordResetRequestService service;

    public PasswordResetRequestController(
            IPasswordResetRequestService service) {
        this.service = service;
    }

    // STEP 1: Request password reset
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestReset(
            @RequestBody ResetRequest request) {

        service.requestPasswordReset(request.email());

        return ResponseEntity.ok(Map.of(
                "message",
                "If an account exists for that email, a password-reset request has been submitted."
        ));
    }

    // STEP 2: Check request status
    @GetMapping("/status/{token}")
    public ResponseEntity<Map<String, String>> checkStatus(
            @PathVariable UUID token) {

        String status = service.getRequestStatus(token);

        return ResponseEntity.ok(Map.of("status", status));
    }

    // STEP 3: Complete password reset
    @PostMapping("/complete")
    public ResponseEntity<Map<String, String>> completeReset(
            @RequestBody CompleteResetRequest request) {

        service.completePasswordReset(
                request.token(),
                request.newPassword()
        );

        return ResponseEntity.ok(Map.of(
                "message",
                "Password reset completed successfully."
        ));
    }

    // ADMIN: View password-reset history
    @GetMapping("/admin/history")
    public ResponseEntity<List<ResetHistoryResponse>> getPasswordResetHistory() {

        List<ResetHistoryResponse> history =
                service.getPasswordResetHistory()
                        .stream()
                        .map(ResetHistoryResponse::from)
                        .toList();

        return ResponseEntity.ok(history);
    }

    // ADMIN: View pending requests
    @GetMapping("/admin/pending")
    public ResponseEntity<List<PendingResetResponse>> getPendingRequests() {

        List<PendingResetResponse> requests =
                service.getPendingRequests()
                        .stream()
                        .map(PendingResetResponse::from)
                        .toList();

        return ResponseEntity.ok(requests);
    }

    // ADMIN: Approve request
    @PatchMapping("/admin/{id}/approve")
    public ResponseEntity<Map<String, String>> approveRequest(
            @PathVariable Long id) {

        service.approveRequest(id);

        return ResponseEntity.ok(Map.of(
                "message",
                "Password-reset request approved."
        ));
    }

    // ADMIN: Reject request
    @PatchMapping("/admin/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectRequest(
            @PathVariable Long id) {

        service.rejectRequest(id);

        return ResponseEntity.ok(Map.of(
                "message",
                "Password-reset request rejected."
        ));
    }

    public record ResetRequest(String email) {
    }

    public record CompleteResetRequest(
            UUID token,
            String newPassword) {
    }

    // Response for pending requests
    public record PendingResetResponse(
            Long id,
            String username,
            String name,
            String email,
            String status,
            String createdAt) {

        static PendingResetResponse from(PasswordResetRequest request) {

            var staff = request.getStaffProfile();

            return new PendingResetResponse(
                    request.getId(),
                    staff.getUsername(),
                    staff.getName(),
                    staff.getEmail(),
                    request.getStatus().name(),
                    request.getCreatedAt() != null
                            ? request.getCreatedAt().toString() : null
            );
        }
    }

    // Response for request history
    public record ResetHistoryResponse(
            Long id,
            String username,
            String name,
            String email,
            String status,
            String createdAt,
            String reviewedAt,
            String completedAt,
            String expiresAt) {

        static ResetHistoryResponse from(PasswordResetRequest request) {

            var staff = request.getStaffProfile();

            String status = request.getStatus().name();

            if ("APPROVED".equals(status)
                    && request.getExpiresAt() != null
                    && !LocalDateTime.now().isBefore(request.getExpiresAt())) {
                status = "EXPIRED";
            }

            return new ResetHistoryResponse(
                    request.getId(),
                    staff.getUsername(),
                    staff.getName(),
                    staff.getEmail(),
                    status,
                    request.getCreatedAt() != null
                            ? request.getCreatedAt().toString() : null,
                    request.getReviewedAt() != null
                            ? request.getReviewedAt().toString() : null,
                    request.getCompletedAt() != null
                            ? request.getCompletedAt().toString() : null,
                    request.getExpiresAt() != null
                            ? request.getExpiresAt().toString() : null
            );
        }
    }
}


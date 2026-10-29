package za.ac.cput.logisticmanagementsystem.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_requests")
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_profile_id", nullable = false)
    private StaffProfile staffProfile;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PasswordResetStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime completedAt;

    protected PasswordResetRequest() {
    }

    public PasswordResetRequest(Builder builder) {
        this.staffProfile = builder.staffProfile;
        this.token = builder.token;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.expiresAt = builder.expiresAt;
    }

    public Long getId() {
        return id;
    }

    public StaffProfile getStaffProfile() {
        return staffProfile;
    }

    public UUID getToken() {
        return token;
    }

    public PasswordResetStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public static class Builder {
        private StaffProfile staffProfile;
        private UUID token;
        private PasswordResetStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;

        public Builder staffProfile(StaffProfile staffProfile) {
            this.staffProfile = staffProfile;
            return this;
        }

        public Builder token(UUID token) {
            this.token = token;
            return this;
        }

        public Builder status(PasswordResetStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public PasswordResetRequest build() {
            return new PasswordResetRequest(this);
        }
    }
    public void approve(LocalDateTime reviewedAt) {
        this.status = PasswordResetStatus.APPROVED;
        this.reviewedAt = reviewedAt;
    }

    public void reject(LocalDateTime reviewedAt) {
        this.status = PasswordResetStatus.REJECTED;
        this.reviewedAt = reviewedAt;
    }

    public void complete(LocalDateTime completedAt) {
        this.status = PasswordResetStatus.COMPLETED;
        this.completedAt = completedAt;
    }

}


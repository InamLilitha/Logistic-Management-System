package za.ac.cput.logisticmanagementsystem.service;

import java.util.List;
import java.util.UUID;

import za.ac.cput.logisticmanagementsystem.domain.PasswordResetRequest;

public interface IPasswordResetRequestService {

    void requestPasswordReset(String email);

    List<PasswordResetRequest> getPendingRequests();

    List<PasswordResetRequest> getPasswordResetHistory();

    void approveRequest(Long id);

    void rejectRequest(Long id);

    String getRequestStatus(UUID token);

    void completePasswordReset(UUID token, String newPassword);
}


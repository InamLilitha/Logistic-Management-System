package za.ac.cput.logisticmanagementsystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.service.StaffProfileService;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class StaffProfileController {

    private final StaffProfileService service;

    public StaffProfileController(StaffProfileService service) {
        this.service = service;
    }
    public record UpdateStaffRequest(
            String name,
            String username,
            String email
    ) {
    }

    @GetMapping
    public List<StaffResponse> getStaffProfiles() {
        return service.getStaffProfiles()
                .stream()
                .map(StaffResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> createStaff(
            @RequestBody CreateStaffRequest request) {

        try {
            StaffProfile profile = service.createStaff(
                    request.name(),
                    request.username(),
                    request.email(),
                    request.password()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(StaffResponse.from(profile));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    @PatchMapping("/{id}/access")
    public ResponseEntity<?> setStaffAccess(
            @PathVariable Long id,
            @RequestBody AccessRequest request) {

        try {
            StaffProfile profile = service.setStaffAccess(
                    id,
                    request.active()
            );

            return ResponseEntity.ok(StaffResponse.from(profile));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable("id") Long id,
            @RequestBody UpdateStaffRequest request) {

        try {
            StaffProfile profile = service.updateStaff(
                    id,
                    request.name(),
                    request.username(),
                    request.email()
            );

            return ResponseEntity.ok(StaffResponse.from(profile));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        try {
            service.deleteStaff(id);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    public record CreateStaffRequest(
            String name,
            String username,
            String email,
            String password) {
    }

    public record AccessRequest(boolean active) {
    }

    public record StaffResponse(
            Long id,
            String username,
            String name,
            String email,
            String role,
            boolean active) {

        static StaffResponse from(StaffProfile profile) {
            return new StaffResponse(
                    profile.getId(),
                    profile.getUsername(),
                    profile.getName(),
                    profile.getEmail(),
                    profile.getRole().toLowerCase(),
                    profile.isActive()
            );
        }
    }
}


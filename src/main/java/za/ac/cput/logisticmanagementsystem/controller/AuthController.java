package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.service.StaffProfileService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AuthController {

    private final StaffProfileService service;

    public AuthController(StaffProfileService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        StaffProfile profile = service.authenticate(request.username(), request.password());
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(UserResponse.from(profile));
    }

    public record LoginRequest(String username, String password) {
    }

    public record UserResponse(Long id, String username, String name, String role) {
        static UserResponse from(StaffProfile profile) {
            return new UserResponse(profile.getId(), profile.getUsername(), profile.getName(), profile.getRole().toLowerCase());
        }
    }
}

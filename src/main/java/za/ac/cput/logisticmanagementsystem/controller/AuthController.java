package za.ac.cput.logisticmanagementsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.service.StaffProfileService;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StaffProfileService service;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            StaffProfileService service,
            SecurityContextRepository securityContextRepository) {
        this.service = service;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        if (request.username() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }

        StaffProfile profile = service.authenticate(
                request.username().trim(),
                request.password()
        );

        if (profile == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = profile.getRole().toUpperCase();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        profile.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return ResponseEntity.ok(UserResponse.from(profile));
    }

    public record LoginRequest(String username, String password) {
    }

    public record UserResponse(
            Long id,
            String username,
            String name,
            String role
    ) {
        static UserResponse from(StaffProfile profile) {
            return new UserResponse(
                    profile.getId(),
                    profile.getUsername(),
                    profile.getName(),
                    profile.getRole().toLowerCase()
            );
        }
    }


}

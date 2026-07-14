package com.unaj.detectface.auth;

import com.unaj.detectface.auth.dto.AuthResponse;
import com.unaj.detectface.auth.dto.LoginRequest;
import com.unaj.detectface.auth.dto.RegisterRequest;
import com.unaj.detectface.auth.dto.TokenRefreshRequest;
import com.unaj.detectface.auth.dto.TokenRefreshResponse;
import com.unaj.detectface.common.ApiResponse;
import com.unaj.detectface.exception.BadRequestException;
import com.unaj.detectface.exception.ResourceNotFoundException;
import com.unaj.detectface.role.entity.Role;
import com.unaj.detectface.role.repository.RoleRepository;
import com.unaj.detectface.security.JwtService;
import com.unaj.detectface.security.UserPrincipal;
import com.unaj.detectface.user.entity.User;
import com.unaj.detectface.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email '" + request.getEmail() + "' ya está registrado");
        }

        Role defaultRole = roleRepository.findByNombre("USUARIO")
                .orElseThrow(() -> new ResourceNotFoundException("Rol por defecto 'USUARIO' no encontrado"));

        User user = User.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .role(defaultRole)
                .build();

        User savedUser = userRepository.save(user);
        UserPrincipal principal = new UserPrincipal(savedUser);

        String accessToken = jwtService.generateToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(savedUser.getId())
                .nombres(savedUser.getNombres())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getNombre())
                .build();

        return ResponseEntity.ok(ApiResponse.success(authResponse, "Usuario registrado y autenticado con éxito"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = jwtService.generateToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .nombres(user.getNombres())
                .email(user.getEmail())
                .role(user.getRole().getNombre())
                .build();

        return ResponseEntity.ok(ApiResponse.success(authResponse, "Autenticación exitosa"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractUsername(refreshToken);

        if (email != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);

                TokenRefreshResponse refreshResponse = TokenRefreshResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();

                return ResponseEntity.ok(ApiResponse.success(refreshResponse, "Token refrescado con éxito"));
            }
        }
        throw new BadRequestException("Refresh token inválido o expirado");
    }
}

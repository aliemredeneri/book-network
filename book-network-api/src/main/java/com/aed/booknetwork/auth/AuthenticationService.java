package com.aed.booknetwork.auth;

import com.aed.booknetwork.email.EmailService;
import com.aed.booknetwork.role.RoleRepository;
import com.aed.booknetwork.user.Token;
import com.aed.booknetwork.user.TokenRepository;
import com.aed.booknetwork.user.User;
import com.aed.booknetwork.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public void register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(userRole))
                .enabled(true)
                .accountLocked(false)
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) {
        var newToken = generateAndSaveToken(user);

    }

    private String generateAndSaveToken(User user) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tokenBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 20; i++) {
            tokenBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        String token = tokenBuilder.toString();

        Token tokenEntity = Token.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        tokenRepository.save(tokenEntity);
        return token;
    }
}

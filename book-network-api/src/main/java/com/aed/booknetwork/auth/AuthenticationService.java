package com.aed.booknetwork.auth;

import com.aed.booknetwork.email.EmailService;
import com.aed.booknetwork.email.EmailTemplateName;
import com.aed.booknetwork.role.RoleRepository;
import com.aed.booknetwork.security.JwtService;
import com.aed.booknetwork.user.Token;
import com.aed.booknetwork.user.TokenRepository;
import com.aed.booknetwork.user.User;
import com.aed.booknetwork.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(userRole))
                .enabled(false)
                .accountLocked(false)
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveToken(user);

        emailService.sendEmail(user.getEmail(), user.getFullName(), EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl,
                newToken, "Account activation");
    }

    private String generateAndSaveToken(User user) {
        String characters = "0123456789";
        StringBuilder tokenBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    //@Transactional
    public void activateAccount(String request) throws MessagingException {
        var token = tokenRepository.findByToken(request)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            sendValidationEmail(token.getUser());
            throw new RuntimeException("Token expired");
        }

        var user = userRepository.findById(token.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
        token.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }
}

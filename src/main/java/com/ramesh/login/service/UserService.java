package com.ramesh.login.service;

import com.ramesh.login.dto.AuthRequest;
import com.ramesh.login.dto.AuthResponse;
import com.ramesh.login.model.User;
import com.ramesh.login.repository.UserRepository;
import com.ramesh.login.security.JwtUtil;
import com.ramesh.login.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtil jwtUtil;

    @Autowired
    SecurityConfig securityConfig;

//    public String registerUser(User user) {
//        user.setPassword(encoder.encode(user.getPassword()));
//        repo.save(user);
//        return "User registered successfully";
//    }

    public AuthResponse registerUser(User user) {
        // 1. Encode the password
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        user.setConfirmpassword(securityConfig.passwordEncoder().encode(user.getConfirmpassword()));

        // 2. Save the user to the database
        repo.save(user);

        // 3. Generate a JWT token for the new user
        String token = jwtUtil.generateToken(user.getUsername());

        // 4. Return the token as response
        return new AuthResponse(token);
    }



    public Map<String, String> authenticateUser(AuthRequest req) {
        User user = repo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return Map.of("token", token);
    }
}


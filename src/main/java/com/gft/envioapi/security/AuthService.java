package com.gft.envioapi.security;

import com.gft.envioapi.dto.LoginRequest;
import com.gft.envioapi.dto.LoginResponse;
import com.gft.envioapi.entity.User;
import com.gft.envioapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getSenha(), user.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        // Se for enum Role { ADMIN, USER }:
        String role = user.getRole().name();     // ou user.getRole() se já for String
        String token = jwtService.generateToken(user.getLogin(), List.of(role));

        return new LoginResponse(token, user.getLogin());
    }

    public User registrar(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        return userRepository.save(user);
    }
}

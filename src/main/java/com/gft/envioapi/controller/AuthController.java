package com.gft.envioapi.controller;

import com.gft.envioapi.dto.LoginRequest;
import com.gft.envioapi.dto.LoginResponse;
import com.gft.envioapi.entity.User;
import com.gft.envioapi.entity.UserRole;
import com.gft.envioapi.repository.UserRepository;
import com.gft.envioapi.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registrar(@RequestBody User user) {
        return ResponseEntity.ok(authService.registrar(user));
    }

    @PostMapping("/create-admin")
    public ResponseEntity<String> criarAdmin() {
        try {
            var adminOpt = userRepository.findByLogin("admin");
            if (adminOpt.isPresent()) {
                return ResponseEntity.ok("Usuário admin já existe");
            }
            
            User admin = new User();
            admin.setLogin("admin");
            admin.setSenha("admin123");
            admin.setRole(UserRole.ADMIN);
            
            authService.registrar(admin);
            return ResponseEntity.ok("Usuário admin criado com sucesso! Login: admin, Senha: admin123");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao criar admin: " + e.getMessage());
        }
    }
}

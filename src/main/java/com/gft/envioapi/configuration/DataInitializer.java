package com.gft.envioapi.configuration;

import com.gft.envioapi.entity.User;
import com.gft.envioapi.entity.UserRole;
import com.gft.envioapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Verifica se já existe um usuário admin
            if (userRepository.findByLogin("admin").isEmpty()) {
                log.info("═══════════════════════════════════════════════════════");
                log.info("🔧 Criando usuário admin padrão...");
                
                User admin = new User();
                admin.setLogin("admin");
                String senhaPlana = "admin123";
                admin.setSenha(passwordEncoder.encode(senhaPlana));
                admin.setRole(UserRole.ADMIN);
                
                User saved = userRepository.save(admin);
                
                log.info("✅ Usuário admin criado com sucesso!");
                log.info("   ID: {}", saved.getUserId());
                log.info("   Login: admin");
                log.info("   Senha: admin123");
                log.info("   Role: {}", saved.getRole());
                log.info("═══════════════════════════════════════════════════════");
            } else {
                User existing = userRepository.findByLogin("admin").get();
                log.info("ℹ️  Usuário admin já existe no banco de dados.");
                log.info("   ID: {}", existing.getUserId());
                log.info("   Login: {}", existing.getLogin());
                log.info("   Role: {}", existing.getRole());
            }
        } catch (Exception e) {
            log.error("❌ Erro ao criar usuário admin: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}


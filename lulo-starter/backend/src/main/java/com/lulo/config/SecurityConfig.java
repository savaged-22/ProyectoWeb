package com.lulo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * BCrypt con factor de coste 12 (recomendado para producción).
     * Úsalo en cualquier servicio con: @Autowired PasswordEncoder passwordEncoder;
     * - Al registrar:  usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
     * - Al verificar:  passwordEncoder.matches(rawPassword, usuario.getPasswordHash());
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

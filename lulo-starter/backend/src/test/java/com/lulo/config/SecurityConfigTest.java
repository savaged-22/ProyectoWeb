package com.lulo.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_retornaConfiguracionCorrecta() {
        SecurityConfig securityConfig = new SecurityConfig();

        assertDoesNotThrow(() -> securityConfig.corsConfigurationSource());
    }
}
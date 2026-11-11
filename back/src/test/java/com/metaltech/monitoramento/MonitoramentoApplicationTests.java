package com.metaltech.monitoramento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MonitoramentoApplicationTests {

    @Test
    @DisplayName("A aplicação deve subir com todas as configurações carregadas")
    void contextLoads() {
    }
}
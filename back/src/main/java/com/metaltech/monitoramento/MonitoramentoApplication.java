package com.metaltech.monitoramento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

/**
 * Ponto de entrada da API de Monitoramento Industrial MetalTech.
 * VIA_DTO: páginas (Page) são devolvidas em um JSON estável:
 * { "content": [...], "page": { "size", "number", "totalElements", "totalPages" } }
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class MonitoramentoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoramentoApplication.class, args);
    }
}
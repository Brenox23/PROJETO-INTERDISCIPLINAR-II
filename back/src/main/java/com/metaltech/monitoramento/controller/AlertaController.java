package com.metaltech.monitoramento.controller;

import com.metaltech.monitoramento.dto.response.AlertaResponse;
import com.metaltech.monitoramento.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Alertas gerados pelas leituras fora do padrão")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    @Operation(summary = "Alertas abertos (de uma esteira ou de todas)")
    public List<AlertaResponse> listarAbertos(@RequestParam(required = false) Long esteiraId) {
        return alertaService.listarAbertos(esteiraId);
    }

    @GetMapping("/historico")
    @Operation(summary = "Histórico de alertas de uma esteira (paginado)")
    public Page<AlertaResponse> historico(@RequestParam Long esteiraId,
                                          @PageableDefault(size = 20) Pageable pageable) {
        return alertaService.historico(esteiraId, pageable);
    }

    @PatchMapping("/{id}/resolver")
    @Operation(summary = "Marca um alerta como resolvido (ADMIN/OPERADOR)")
    public AlertaResponse resolver(@PathVariable Long id) {
        return alertaService.resolver(id);
    }
}
package com.metaltech.monitoramento.controller;

import com.metaltech.monitoramento.dto.response.ContagemResponse;
import com.metaltech.monitoramento.dto.response.NivelAtualResponse;
import com.metaltech.monitoramento.service.IndicadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/indicadores")
@Tag(name = "Indicadores", description = "Dados em tempo (quase) real para o dashboard e a RA")
public class IndicadorController {

    private final IndicadorService indicadorService;

    public IndicadorController(IndicadorService indicadorService) {
        this.indicadorService = indicadorService;
    }

    @GetMapping("/contagem")
    @Operation(summary = "Contagem de peças por tipo (total ou em um período)")
    public ContagemResponse contagem(
            @RequestParam Long esteiraId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return indicadorService.contagem(esteiraId, inicio, fim);
    }

    @GetMapping("/nivel-atual")
    @Operation(summary = "Nível atual do reservatório com o alerta avaliado")
    public NivelAtualResponse nivelAtual(@RequestParam Long esteiraId) {
        return indicadorService.nivelAtual(esteiraId);
    }
}
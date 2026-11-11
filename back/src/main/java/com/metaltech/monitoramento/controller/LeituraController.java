package com.metaltech.monitoramento.controller;

import com.metaltech.monitoramento.dto.request.LeituraNivelRequest;
import com.metaltech.monitoramento.dto.request.LeituraPecaRequest;
import com.metaltech.monitoramento.dto.response.LeituraResponse;
import com.metaltech.monitoramento.service.LeituraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/leituras")
@Tag(name = "Leituras", description = "Recebimento e consulta das leituras dos sensores")
public class LeituraController {

    private final LeituraService leituraService;

    public LeituraController(LeituraService leituraService) {
        this.leituraService = leituraService;
    }

    @PostMapping("/pecas")
    @Operation(summary = "Registra uma peça detectada (automação - ADMIN/OPERADOR)")
    public ResponseEntity<LeituraResponse> registrarPeca(@RequestBody @Valid LeituraPecaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leituraService.registrarPeca(request));
    }

    @PostMapping("/niveis")
    @Operation(summary = "Registra uma leitura do nível do reservatório (automação - ADMIN/OPERADOR)")
    public ResponseEntity<LeituraResponse> registrarNivel(@RequestBody @Valid LeituraNivelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leituraService.registrarNivel(request));
    }

    @GetMapping("/pecas")
    @Operation(summary = "Últimas peças registradas (paginado)")
    public Page<LeituraResponse> ultimasPecas(@RequestParam Long esteiraId,
                                              @PageableDefault(size = 20) Pageable pageable) {
        return leituraService.ultimasPecas(esteiraId, pageable);
    }

    @GetMapping("/niveis")
    @Operation(summary = "Últimas leituras do nível do reservatório (paginado)")
    public Page<LeituraResponse> ultimosNiveis(@RequestParam Long esteiraId,
                                               @PageableDefault(size = 20) Pageable pageable) {
        return leituraService.ultimosNiveis(esteiraId, pageable);
    }

    @GetMapping("/historico")
    @Operation(summary = "Histórico completo (peças + nível) em um período, em ordem cronológica")
    public List<LeituraResponse> historico(
            @RequestParam Long esteiraId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return leituraService.historico(esteiraId, inicio, fim);
    }
}
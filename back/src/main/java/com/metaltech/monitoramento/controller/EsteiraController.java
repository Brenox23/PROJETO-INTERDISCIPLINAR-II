package com.metaltech.monitoramento.controller;

import com.metaltech.monitoramento.dto.request.EsteiraRequest;
import com.metaltech.monitoramento.dto.response.EsteiraResponse;
import com.metaltech.monitoramento.service.EsteiraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/esteiras")
@Tag(name = "Esteiras", description = "Cadastro das esteiras de triagem")
public class EsteiraController {

    private final EsteiraService esteiraService;

    public EsteiraController(EsteiraService esteiraService) {
        this.esteiraService = esteiraService;
    }

    @PostMapping
    @Operation(summary = "Cadastra uma esteira (ADMIN)")
    public ResponseEntity<EsteiraResponse> criar(@RequestBody @Valid EsteiraRequest request) {
        EsteiraResponse criada = esteiraService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @Operation(summary = "Lista todas as esteiras")
    public List<EsteiraResponse> listar() {
        return esteiraService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma esteira pelo id")
    public EsteiraResponse buscarPorId(@PathVariable Long id) {
        return esteiraService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma esteira (ADMIN)")
    public EsteiraResponse atualizar(@PathVariable Long id, @RequestBody @Valid EsteiraRequest request) {
        return esteiraService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa uma esteira, preservando o histórico (ADMIN)")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        esteiraService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Reativa uma esteira desativada (ADMIN)")
    public EsteiraResponse ativar(@PathVariable Long id) {
        return esteiraService.ativar(id);
    }
}
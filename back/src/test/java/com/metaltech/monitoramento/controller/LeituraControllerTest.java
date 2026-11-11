package com.metaltech.monitoramento.controller;

import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.repository.EsteiraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de INTEGRAÇÃO: sobe a aplicação inteira (com H2 em memória)
 * e simula requisições HTTP reais, incluindo segurança e permissões.
 * @Transactional desfaz tudo ao final de cada teste (um não interfere no outro).
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LeituraControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EsteiraRepository esteiraRepository;

    private MockMvc mockMvc;
    private Long esteiraId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        esteiraId = esteiraRepository
                .save(new Esteira("Esteira Teste", "Linha Teste", 30.0, 70.0, 90.0))
                .getId();
    }

    private String jsonPeca(Long id, boolean indutivo, boolean capacitivo) {
        return """
                {"esteiraId": %d, "sensorIndutivo": %b, "sensorCapacitivo": %b}
                """.formatted(id, indutivo, capacitivo);
    }

    @Test
    @DisplayName("POST de peça sem login deve retornar 401")
    void deveRecusarSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/leituras/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPeca(esteiraId, true, true)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST de peça com perfil VISUALIZADOR deve retornar 403")
    void deveRecusarPerfilSemPermissao() throws Exception {
        mockMvc.perform(post("/api/leituras/pecas")
                        .with(user("visitante").roles("VISUALIZADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPeca(esteiraId, true, true)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST de peça com perfil OPERADOR deve classificar e retornar 201")
    void deveRegistrarPecaMetalica() throws Exception {
        mockMvc.perform(post("/api/leituras/pecas")
                        .with(user("esteira").roles("OPERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPeca(esteiraId, true, true)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoLeitura").value("PECA"))
                .andExpect(jsonPath("$.tipoPeca").value("METALICA"));
    }

    @Test
    @DisplayName("POST sem esteiraId deve retornar 400 indicando o campo inválido")
    void deveValidarCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/api/leituras/pecas")
                        .with(user("esteira").roles("OPERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sensorIndutivo": true, "sensorCapacitivo": true}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("esteiraId"));
    }

    @Test
    @DisplayName("POST para esteira inexistente deve retornar 404")
    void deveRetornar404ParaEsteiraInexistente() throws Exception {
        mockMvc.perform(post("/api/leituras/pecas")
                        .with(user("esteira").roles("OPERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPeca(999999L, true, true)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Nível crítico deve gerar alerta visível em GET /api/alertas")
    void deveGerarAlertaCritico() throws Exception {
        mockMvc.perform(post("/api/leituras/niveis")
                        .with(user("esteira").roles("OPERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"esteiraId": %d, "distanciaCm": 2}
                                """.formatted(esteiraId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nivelAlerta").value("CRITICO"));

        mockMvc.perform(get("/api/alertas").param("esteiraId", esteiraId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nivel").value("CRITICO"));
    }

    @Test
    @DisplayName("Contagem deve somar peças por tipo e ser pública (sem login)")
    void deveContarPecasPorTipo() throws Exception {
        mockMvc.perform(post("/api/leituras/pecas")
                        .with(user("esteira").roles("OPERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPeca(esteiraId, true, true)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/leituras/pecas")
                        .with(user("esteira").roles("OPERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPeca(esteiraId, false, true)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/indicadores/contagem").param("esteiraId", esteiraId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metalicas").value(1))
                .andExpect(jsonPath("$.naoMetalicas").value(1))
                .andExpect(jsonPath("$.total").value(2));
    }
}
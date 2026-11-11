package com.metaltech.monitoramento.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato PADRÃO de erro devolvido pela API.
 * Front-end, RA e automação sempre recebem erros neste mesmo formato.
 * Exemplo:
 * {
 *   "timestamp": "2026-11-10T21:30:00",
 *   "status": 400,
 *   "erro": "Bad Request",
 *   "mensagem": "Um ou mais campos são inválidos.",
 *   "caminho": "/api/leituras/pecas",
 *   "campos": [ { "campo": "esteiraId", "mensagem": "O id da esteira é obrigatório." } ]
 * }
 */
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoInvalido> campos
) {

    /** Detalhe de um campo que falhou na validação. */
    public record CampoInvalido(String campo, String mensagem) {
    }

    public static ErroResponse de(HttpStatus status, String mensagem, String caminho) {
        return new ErroResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensagem, caminho, List.of());
    }

    public static ErroResponse comCampos(HttpStatus status, String mensagem, String caminho,
                                         List<CampoInvalido> campos) {
        return new ErroResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensagem, caminho, campos);
    }
}
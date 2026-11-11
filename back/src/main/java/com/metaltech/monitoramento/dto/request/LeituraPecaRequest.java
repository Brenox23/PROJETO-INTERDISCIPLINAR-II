package com.metaltech.monitoramento.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * JSON enviado pela automação (Diego) a cada peça detectada.
 * Exemplo:
 * {
 *   "esteiraId": 1,
 *   "sensorIndutivo": true,
 *   "sensorCapacitivo": true,
 *   "registradoEm": "2026-11-10T14:30:00"
 * }
 * O campo registradoEm é opcional: se não vier, usa o horário do servidor.
 */
public record LeituraPecaRequest(

        @NotNull(message = "O id da esteira é obrigatório.")
        Long esteiraId,

        @NotNull(message = "A leitura do sensor indutivo é obrigatória.")
        Boolean sensorIndutivo,

        @NotNull(message = "A leitura do sensor capacitivo é obrigatória.")
        Boolean sensorCapacitivo,

        LocalDateTime registradoEm
) {
}
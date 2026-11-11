package com.metaltech.monitoramento.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

/**
 * JSON enviado pela automação com a distância medida sobre o reservatório.
 * Exemplo:
 * {
 *   "esteiraId": 1,
 *   "distanciaCm": 12.5,
 *   "registradoEm": "2026-11-10T14:30:00"
 * }
 */
public record LeituraNivelRequest(

        @NotNull(message = "O id da esteira é obrigatório.")
        Long esteiraId,

        @NotNull(message = "A distância medida é obrigatória.")
        @PositiveOrZero(message = "A distância não pode ser negativa.")
        Double distanciaCm,

        LocalDateTime registradoEm
) {
}
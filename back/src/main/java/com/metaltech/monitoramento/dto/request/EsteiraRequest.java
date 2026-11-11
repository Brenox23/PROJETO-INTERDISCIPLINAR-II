package com.metaltech.monitoramento.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Dados para cadastrar ou atualizar uma esteira.
 */
public record EsteiraRequest(

        @NotBlank(message = "O nome da esteira é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @Size(max = 150, message = "A localização deve ter no máximo 150 caracteres.")
        String localizacao,

        @NotNull(message = "A altura do reservatório é obrigatória.")
        @Positive(message = "A altura do reservatório deve ser maior que zero.")
        Double alturaReservatorioCm,

        @NotNull(message = "O limite de atenção é obrigatório.")
        @Positive(message = "O limite de atenção deve ser maior que zero.")
        @Max(value = 100, message = "O limite de atenção deve ser no máximo 100.")
        Double limiteAtencaoPercentual,

        @NotNull(message = "O limite crítico é obrigatório.")
        @Positive(message = "O limite crítico deve ser maior que zero.")
        @Max(value = 100, message = "O limite crítico deve ser no máximo 100.")
        Double limiteCriticoPercentual
) {
}
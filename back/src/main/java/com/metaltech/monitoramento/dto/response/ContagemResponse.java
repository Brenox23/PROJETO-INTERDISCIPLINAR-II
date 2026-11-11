package com.metaltech.monitoramento.dto.response;

import java.time.LocalDateTime;

/**
 * Contagem de peças por tipo, consumida pelo dashboard e pela RA.
 */
public record ContagemResponse(
        Long esteiraId,
        String esteiraNome,
        long metalicas,
        long naoMetalicas,
        long indefinidas,
        long total,
        double percentualMetalicas,
        double percentualNaoMetalicas,
        LocalDateTime atualizadoEm
) {

    /**
     * Fábrica que calcula total e percentuais a partir das contagens.
     */
    public static ContagemResponse de(Long esteiraId, String esteiraNome,
                                      long metalicas, long naoMetalicas, long indefinidas) {
        long total = metalicas + naoMetalicas + indefinidas;
        double percMetal = (total == 0) ? 0.0 : arredondar(metalicas * 100.0 / total);
        double percNaoMetal = (total == 0) ? 0.0 : arredondar(naoMetalicas * 100.0 / total);
        return new ContagemResponse(esteiraId, esteiraNome, metalicas, naoMetalicas,
                indefinidas, total, percMetal, percNaoMetal, LocalDateTime.now());
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
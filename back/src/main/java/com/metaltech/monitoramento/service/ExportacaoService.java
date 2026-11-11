package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.dto.response.LeituraResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Gera o CSV do histórico de leituras para a Ciência de Dados.
 * Separador: vírgula | Decimal: ponto (padrão lido direto pelo pandas).
 */
@Service
public class ExportacaoService {

    private static final String CABECALHO = String.join(",",
            "id", "esteira_id", "tipo_leitura", "registrado_em", "tipo_peca",
            "sensor_indutivo", "sensor_capacitivo", "distancia_cm", "percentual", "nivel_alerta");

    private final LeituraService leituraService;

    public ExportacaoService(LeituraService leituraService) {
        this.leituraService = leituraService;
    }

    public String exportarCsv(Long esteiraId, LocalDateTime inicio, LocalDateTime fim) {
        List<LeituraResponse> leituras = leituraService.historico(esteiraId, inicio, fim);

        StringBuilder csv = new StringBuilder(CABECALHO).append('\n');
        for (LeituraResponse leitura : leituras) {
            csv.append(String.join(",",
                    valor(leitura.id()),
                    valor(leitura.esteiraId()),
                    valor(leitura.tipoLeitura()),
                    valor(leitura.registradoEm()),
                    valor(leitura.tipoPeca()),
                    valor(leitura.sensorIndutivo()),
                    valor(leitura.sensorCapacitivo()),
                    valor(leitura.distanciaCm()),
                    valor(leitura.percentual()),
                    valor(leitura.nivelAlerta())
            )).append('\n');
        }
        return csv.toString();
    }

    /** Campos nulos viram célula vazia (o pandas lê como NaN). */
    private String valor(Object campo) {
        return (campo == null) ? "" : campo.toString();
    }
}
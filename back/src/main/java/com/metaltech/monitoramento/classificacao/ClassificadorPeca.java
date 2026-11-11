package com.metaltech.monitoramento.classificacao;

import com.metaltech.monitoramento.domain.enums.TipoPeca;

/**
 * POLIMORFISMO (padrão Strategy): define O QUE o classificador faz,
 * sem fixar COMO. Outra regra de classificação pode ser criada
 * implementando esta interface, sem alterar o restante do sistema.
 */
public interface ClassificadorPeca {

    TipoPeca classificar(boolean sensorIndutivo, boolean sensorCapacitivo);
}
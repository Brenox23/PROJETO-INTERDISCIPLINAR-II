package com.metaltech.monitoramento.domain;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;

/**
 * Contrato para tudo que pode ser avaliado quanto à necessidade de alerta.
 * Cada classe que implementa decide COMO se avalia (polimorfismo).
 */
public interface Avaliavel {

    NivelAlerta avaliarAlerta();
}
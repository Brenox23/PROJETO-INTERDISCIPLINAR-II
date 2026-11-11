package com.metaltech.monitoramento.exception;

/**
 * Lançada quando uma leitura enviada pela esteira não pode ser aceita
 * (ex.: esteira desativada). Vira HTTP 400.
 */
public class LeituraInvalidaException extends RuntimeException {

    public LeituraInvalidaException(String mensagem) {
        super(mensagem);
    }
}
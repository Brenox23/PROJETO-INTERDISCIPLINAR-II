package com.metaltech.monitoramento.exception;

/**
 * Lançada quando um recurso buscado não existe (vira HTTP 404).
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(String.format("Recurso '%s' com id %d não encontrado.", recurso, id));
    }
}
package com.metaltech.monitoramento.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Captura as exceções lançadas em qualquer controller e as converte
 * no formato padrão ErroResponse, com o status HTTP correto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404 - recurso inexistente (ex.: esteira com id que não existe). */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex,
                                                            HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /** 400 - leitura recusada ou regra de domínio violada. */
    @ExceptionHandler({LeituraInvalidaException.class, IllegalArgumentException.class})
    public ResponseEntity<ErroResponse> tratarRequisicaoInvalida(RuntimeException ex,
                                                                 HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    /** 409 - conflito de estado (ex.: nome/e-mail já cadastrado, alerta já resolvido). */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> tratarConflito(IllegalStateException ex,
                                                       HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    /** 400 - campos do JSON que não passaram no Bean Validation (@NotNull, @Size...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex,
                                                        HttpServletRequest req) {
        List<ErroResponse.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErroResponse.CampoInvalido(erro.getField(), erro.getDefaultMessage()))
                .toList();
        ErroResponse corpo = ErroResponse.comCampos(HttpStatus.BAD_REQUEST,
                "Um ou mais campos são inválidos.", req.getRequestURI(), campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    /** 400 - JSON ausente ou mal formatado. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarJsonInvalido(HttpMessageNotReadableException ex,
                                                           HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST,
                "O corpo da requisição está ausente ou em formato JSON inválido.", req);
    }

    /** 400 - parâmetro com tipo errado (ex.: /api/esteiras/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarTipoInvalido(MethodArgumentTypeMismatchException ex,
                                                           HttpServletRequest req) {
        String mensagem = String.format("O parâmetro '%s' recebeu um valor inválido.", ex.getName());
        return responder(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    /** 400 - parâmetro obrigatório ausente na URL. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroAusente(MissingServletRequestParameterException ex,
                                                               HttpServletRequest req) {
        String mensagem = String.format("O parâmetro '%s' é obrigatório.", ex.getParameterName());
        return responder(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    /** 404 - rota que não existe. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> tratarRotaInexistente(NoResourceFoundException ex,
                                                              HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, "Endpoint não encontrado.", req);
    }

    /** 405 - método HTTP não suportado (ex.: DELETE onde só existe GET). */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex,
                                                                 HttpServletRequest req) {
        String mensagem = String.format("O método %s não é suportado neste endpoint.", ex.getMethod());
        return responder(HttpStatus.METHOD_NOT_ALLOWED, mensagem, req);
    }

    /** 500 - qualquer erro inesperado. O detalhe vai para o log, não para o cliente. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {}", req.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado. Tente novamente mais tarde.", req);
    }

    private ResponseEntity<ErroResponse> responder(HttpStatus status, String mensagem,
                                                   HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ErroResponse.de(status, mensagem, req.getRequestURI()));
    }
}
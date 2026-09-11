package br.com.fiap.solin.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex,
                                                            HttpServletRequest req) {
        var erro = ErroResponse.simples(
                HttpStatus.NOT_FOUND.value(),
                "Nao encontrado",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException ex,
                                                           HttpServletRequest req) {
        var erro = ErroResponse.simples(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Regra de negocio violada",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex,
                                                        HttpServletRequest req) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        var erro = ErroResponse.comDetalhes(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validacao",
                "Um ou mais campos contem valores invalidos",
                req.getRequestURI(),
                detalhes
        );
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarIntegridade(DataIntegrityViolationException ex,
                                                          HttpServletRequest req) {
        var erro = ErroResponse.simples(
                HttpStatus.CONFLICT.value(),
                "Conflito de dados",
                "Operacao viola uma restricao do banco de dados (provavelmente um campo unico duplicado).",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarGenerica(Exception ex, HttpServletRequest req) {
        var erro = ErroResponse.simples(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado: " + ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}

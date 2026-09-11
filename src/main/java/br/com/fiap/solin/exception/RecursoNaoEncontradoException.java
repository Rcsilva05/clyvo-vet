package br.com.fiap.solin.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(String.format("%s com id %d nao encontrado(a).", recurso, id));
    }
}

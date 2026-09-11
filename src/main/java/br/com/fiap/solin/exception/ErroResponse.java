package br.com.fiap.solin.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        List<String> detalhes
) {
    public static ErroResponse simples(int status, String erro, String mensagem, String path) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, path, null);
    }

    public static ErroResponse comDetalhes(int status, String erro, String mensagem,
                                           String path, List<String> detalhes) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, path, detalhes);
    }
}

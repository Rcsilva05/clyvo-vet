package br.com.fiap.solin.web;

import br.com.fiap.solin.exception.RecursoNaoEncontradoException;
import br.com.fiap.solin.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tratamento de excecoes exclusivo para as telas (pacote web.*), com uma
 * pagina de erro HTML legivel em vez do JSON devolvido pelo
 * GlobalExceptionHandler (que e um @RestControllerAdvice global e, sem
 * este advice mais especifico, tambem capturaria as rotas MVC).
 */
@ControllerAdvice(basePackages = "br.com.fiap.solin.web")
public class WebExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String tratarNaoEncontrado(RecursoNaoEncontradoException ex, Model model) {
        model.addAttribute("titulo", "Não encontrado");
        model.addAttribute("mensagem", ex.getMessage());
        return "erro";
    }

    @ExceptionHandler(RegraNegocioException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String tratarRegraNegocio(RegraNegocioException ex, Model model) {
        model.addAttribute("titulo", "Não foi possível concluir");
        model.addAttribute("mensagem", ex.getMessage());
        return "erro";
    }

    // Fallback para qualquer excecao inesperada nas telas MVC (pacote web.*).
    // Sem isso, um erro nao mapeado acima cai na pagina generica "Whitelabel"
    // do Spring Boot em vez da tela de erro estilizada do SOLIN, o que e
    // confuso para o usuario e nao mostra nada aproveitavel na interface.
    // A mensagem completa ainda vai para o log do servidor (console do
    // IntelliJ) para diagnostico.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String tratarErroInesperado(Exception ex, Model model) {
        org.slf4j.LoggerFactory.getLogger(WebExceptionHandler.class)
                .error("Erro inesperado em uma tela do SOLIN", ex);
        model.addAttribute("titulo", "Algo deu errado");
        model.addAttribute("mensagem",
                "Ocorreu um erro inesperado ao carregar esta página. Tente novamente em instantes.");
        return "erro";
    }
}

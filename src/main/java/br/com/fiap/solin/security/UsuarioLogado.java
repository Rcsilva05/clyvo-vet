package br.com.fiap.solin.security;

import br.com.fiap.solin.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Pequeno helper para recuperar o {@link Usuario} autenticado na
 * requisicao atual. Centraliza o acesso ao SecurityContext para que os
 * controllers MVC nao precisem repetir esse "boilerplate".
 */
@Component
public class UsuarioLogado {

    public Usuario obter() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new IllegalStateException("Nenhum usuario autenticado na sessao atual.");
        }
        return usuario;
    }
}

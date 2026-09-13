package br.com.fiap.solin.security;

import br.com.fiap.solin.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ponte entre o Spring Security e a tabela TB_USUARIO. O Spring Security
 * chama {@link #loadUserByUsername(String)} a cada tentativa de login,
 * usando o e-mail informado no formulario.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // O cadastro grava o email sempre em minusculo (ver CadastroService).
        // Sem essa mesma normalizacao aqui, um login com o email digitado em
        // outra caixa (ex.: "Nome@Gmail.com") nao bate com o registro salvo
        // e falha mesmo com a senha correta.
        String emailNormalizado = email.trim().toLowerCase();
        return usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + emailNormalizado));
    }
}

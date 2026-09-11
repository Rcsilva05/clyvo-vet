package br.com.fiap.solin.service;

import br.com.fiap.solin.entity.Tutor;
import br.com.fiap.solin.entity.Usuario;
import br.com.fiap.solin.enums.Perfil;
import br.com.fiap.solin.exception.RegraNegocioException;
import br.com.fiap.solin.repository.TutorRepository;
import br.com.fiap.solin.repository.UsuarioRepository;
import br.com.fiap.solin.web.form.CadastroForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Responsavel pelo auto-cadastro publico de tutores (tela "Criar conta").
 *
 * Diferente do TutorService (usado pela API REST, que so cria o registro
 * de contato em TB_TUTOR), aqui criamos as DUAS linhas necessarias para
 * que a pessoa consiga logar imediatamente: o registro de contato
 * (TB_TUTOR) e o registro de acesso (TB_USUARIO, com senha em hash
 * BCrypt e perfil TUTOR). As duas gravacoes acontecem na mesma
 * transacao: se uma falhar, a outra tambem e desfeita, evitando um
 * tutor "orfao" sem login ou um usuario sem tutor vinculado.
 */
@Service
@RequiredArgsConstructor
public class CadastroService {

    private final TutorRepository tutorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void cadastrarTutor(CadastroForm form) {
        String email = form.getEmail().trim().toLowerCase();

        if (tutorRepository.existsByEmail(email) || usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Já existe uma conta cadastrada com este email.");
        }

        Tutor tutor = Tutor.builder()
                .nome(form.getNome())
                .email(email)
                .telefone(form.getTelefone())
                .dataCadastro(LocalDate.now())
                .build();
        Tutor tutorSalvo = tutorRepository.save(tutor);

        Usuario usuario = Usuario.builder()
                .nome(form.getNome())
                .email(email)
                .senhaHash(passwordEncoder.encode(form.getSenha()))
                .perfil(Perfil.TUTOR)
                .ativo(true)
                .tutor(tutorSalvo)
                .build();
        usuarioRepository.save(usuario);
    }
}

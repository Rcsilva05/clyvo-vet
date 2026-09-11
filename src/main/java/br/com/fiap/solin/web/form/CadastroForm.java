package br.com.fiap.solin.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Formulario publico de criacao de conta (auto-cadastro de tutor).
 * Qualquer pessoa pode acessar "/cadastro" (sem estar logada) e criar
 * sua propria conta informando nome, email e senha. A conta criada e
 * sempre do perfil TUTOR - nao existe auto-cadastro de veterinario,
 * que continua sendo uma conta de responsabilidade da clinica.
 */
@Data
public class CadastroForm {

    @NotBlank(message = "Informe seu nome")
    @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
    private String nome;

    @NotBlank(message = "Informe seu email")
    @Email(message = "Email em formato inválido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Informe seu telefone")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$",
            message = "Telefone em formato inválido. Ex: (11) 91234-5678")
    private String telefone;

    @NotBlank(message = "Crie uma senha")
    @Size(min = 6, max = 100, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    @NotBlank(message = "Confirme a senha")
    private String confirmarSenha;
}

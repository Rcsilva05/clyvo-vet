package br.com.fiap.solin.web;

import br.com.fiap.solin.exception.RegraNegocioException;
import br.com.fiap.solin.service.CadastroService;
import br.com.fiap.solin.web.form.CadastroForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Paginas de suporte que nao pertencem a nenhuma area especifica:
 * raiz do site, tela de login, criacao de conta (auto-cadastro publico
 * de tutor) e tela de acesso negado.
 */
@Controller
@RequiredArgsConstructor
public class PaginasController {

    private final CadastroService cadastroService;

    @GetMapping("/")
    public String raiz() {
        // A propria SecurityConfig cuida do redirecionamento por perfil apos o login;
        // aqui so garantimos que "/" sempre leve a algum lugar coerente.
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        if (!model.containsAttribute("cadastroForm")) {
            model.addAttribute("cadastroForm", new CadastroForm());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("cadastroForm") CadastroForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (!form.getSenha().equals(form.getConfirmarSenha())) {
            bindingResult.rejectValue("confirmarSenha", "diferente", "As senhas não coincidem.");
        }

        if (bindingResult.hasErrors()) {
            return "cadastro";
        }

        try {
            cadastroService.cadastrarTutor(form);
        } catch (RegraNegocioException ex) {
            bindingResult.rejectValue("email", "duplicado", ex.getMessage());
            return "cadastro";
        }

        redirectAttributes.addFlashAttribute("mensagem", "Conta criada com sucesso! Entre com o email e a senha que você acabou de cadastrar.");
        redirectAttributes.addFlashAttribute("tipoFlash", "success");
        return "redirect:/login";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}

package br.com.fiap.solin.web.tutor;

import br.com.fiap.solin.dto.response.AlertaResponse;
import br.com.fiap.solin.dto.response.EspecieResponse;
import br.com.fiap.solin.dto.response.EventoResponse;
import br.com.fiap.solin.dto.response.PetResponse;
import br.com.fiap.solin.enums.NivelAlerta;
import br.com.fiap.solin.exception.RecursoNaoEncontradoException;
import br.com.fiap.solin.repository.AlertaRepository;
import br.com.fiap.solin.security.UsuarioLogado;
import br.com.fiap.solin.service.EspecieService;
import br.com.fiap.solin.service.EventoService;
import br.com.fiap.solin.service.PetService;
import br.com.fiap.solin.web.tutor.form.EventoForm;
import br.com.fiap.solin.web.tutor.form.PetForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Fluxo completo do TUTOR (perfil ROLE_TUTOR):
 *
 *  1) Ver os proprios pets com status de alerta em tempo real
 *  2) Abrir um pet e ver a linha do tempo de eventos + alertas
 *  3) Registrar um novo evento de saude -> dispara reavaliacao das
 *     regras de alerta automaticamente (Strategy ja existente)
 *  4) Cadastrar um novo pet
 *
 * A conta do tutor (nome, email, senha) e criada uma unica vez na tela
 * publica de cadastro ("/cadastro", ver PaginasController) e nao pode
 * ser editada depois de logado - o email cadastrado ali e o login
 * permanente do tutor no SOLIN.
 *
 * Todas as consultas sao restritas ao ID_TUTOR do usuario autenticado,
 * garantindo que um tutor nunca veja ou edite dados de outro tutor.
 */
@Controller
@RequestMapping("/tutor")
@PreAuthorize("hasRole('TUTOR')")
@RequiredArgsConstructor
public class TutorAreaController {

    private final PetService petService;
    private final EventoService eventoService;
    private final EspecieService especieService;
    private final AlertaRepository alertaRepository;
    private final UsuarioLogado usuarioLogado;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        Long tutorId = usuarioLogado.obter().getTutorId();

        // So pets ativos aparecem na tela do dia a dia - um pet
        // "desativado" (soft delete) fica fora daqui, mas o historico
        // dele continua preservado no banco.
        List<PetResponse> pets = petService
                .listarAtivosPorTutor(tutorId, PageRequest.of(0, 50, Sort.by("nome")))
                .getContent();

        // Para cada pet, calcula o "pior" nivel de alerta ainda nao resolvido,
        // para mostrar um badge de status direto no card (sem precisar entrar).
        Map<Long, String> statusPorPet = new java.util.HashMap<>();
        for (PetResponse pet : pets) {
            statusPorPet.put(pet.id(), calcularStatus(pet.id()));
        }

        // Resumo para os cards do topo da tela ("dashboard" do tutor):
        // total de pets, quantos tem alerta ativo agora, e quando foi o
        // ultimo evento registrado entre todos os pets dele.
        long totalPets = pets.size();
        long petsComAlerta = statusPorPet.values().stream()
                .filter(status -> status.equals("amarelo") || status.equals("vermelho"))
                .count();
        String ultimoEvento = pets.stream()
                .map(pet -> eventoService.listarPorPet(pet.id(), null, null, null, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dataHora"))))
                .flatMap(pagina -> pagina.getContent().stream())
                .map(EventoResponse::dataHora)
                .max(java.time.LocalDateTime::compareTo)
                .map(dataHora -> dataHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm")))
                .orElse("Nenhum ainda");

        model.addAttribute("pets", pets);
        model.addAttribute("statusPorPet", statusPorPet);
        model.addAttribute("totalPets", totalPets);
        model.addAttribute("petsComAlerta", petsComAlerta);
        model.addAttribute("ultimoEvento", ultimoEvento);
        return "tutor/inicio";
    }

    @GetMapping("/pets/{id}")
    @Transactional(readOnly = true)
    public String detalhePet(@PathVariable Long id, Model model) {
        PetResponse pet = buscarPetDoTutorLogado(id);

        List<EventoResponse> eventos = eventoService
                .listarPorPet(id, null, null, null, PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "dataHora")))
                .getContent();

        List<AlertaResponse> alertas = alertaRepository.findByPetId(id, PageRequest.of(0, 10,
                        Sort.by(Sort.Direction.DESC, "dataHoraGerado")))
                .map(a -> new AlertaResponse(a.getId(), a.getNivel(), a.getMensagem(), a.getDataHoraGerado(), a.getResolvido(), id, pet.nome()))
                .getContent();

        model.addAttribute("pet", pet);
        model.addAttribute("eventos", eventos);
        model.addAttribute("alertas", alertas);
        return "tutor/pet-detalhe";
    }

    @GetMapping("/pets/novo")
    public String novoPetForm(Model model) {
        List<EspecieResponse> especies = especieService
                .listar(PageRequest.of(0, 50, Sort.by("nome")))
                .getContent();

        model.addAttribute("especies", especies);
        if (!model.containsAttribute("petForm")) {
            model.addAttribute("petForm", new PetForm());
        }
        return "tutor/pet-novo";
    }

    @PostMapping("/pets/novo")
    public String cadastrarPet(@Valid @ModelAttribute("petForm") PetForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("especies", especieService.listar(PageRequest.of(0, 50, Sort.by("nome"))).getContent());
            return "tutor/pet-novo";
        }

        Long tutorId = usuarioLogado.obter().getTutorId();
        PetResponse petCriado = petService.cadastrar(form.paraRequest(tutorId));

        redirectAttributes.addFlashAttribute("mensagem", "Pet " + petCriado.nome() + " cadastrado com sucesso.");
        redirectAttributes.addFlashAttribute("tipoFlash", "success");

        return "redirect:/tutor/pets/" + petCriado.id();
    }

    @GetMapping("/eventos/novo")
    public String novoEventoForm(@RequestParam(required = false) Long petId, Model model) {
        Long tutorId = usuarioLogado.obter().getTutorId();
        List<PetResponse> pets = petService
                .listarPorTutor(tutorId, PageRequest.of(0, 50, Sort.by("nome")))
                .getContent();

        EventoForm form = new EventoForm();
        if (petId != null) {
            form.setPetId(petId);
        }

        model.addAttribute("pets", pets);
        if (!model.containsAttribute("eventoForm")) {
            model.addAttribute("eventoForm", form);
        }
        return "tutor/evento-novo";
    }

    @PostMapping("/eventos/novo")
    public String registrarEvento(@Valid @ModelAttribute("eventoForm") EventoForm form,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // Garante que o pet informado realmente pertence ao tutor logado
        // (protege contra um tutor tentar registrar evento em pet de outro tutor).
        // Só valida a posse do pet se o campo petId já passou nas validacoes de @NotNull;
        // caso contrario o bindingResult.hasErrors() abaixo ja intercepta o fluxo.
        PetResponse pet = null;
        if (form.getPetId() != null) {
            try {
                pet = buscarPetDoTutorLogado(form.getPetId());
            } catch (RecursoNaoEncontradoException ex) {
                bindingResult.rejectValue("petId", "invalido", "Selecione um dos seus pets.");
            }
        }

        if (bindingResult.hasErrors()) {
            Long tutorId = usuarioLogado.obter().getTutorId();
            model.addAttribute("pets", petService.listarPorTutor(tutorId, PageRequest.of(0, 50, Sort.by("nome"))).getContent());
            return "tutor/evento-novo";
        }

        int alertasAntes = contarAlertasAtivos(form.getPetId());
        eventoService.registrar(form.paraRequest());
        int alertasDepois = contarAlertasAtivos(form.getPetId());

        if (alertasDepois > alertasAntes) {
            redirectAttributes.addFlashAttribute("mensagem",
                    "Evento registrado! Atenção: esse registro disparou um novo alerta para " + pet.nome() + ".");
            redirectAttributes.addFlashAttribute("tipoFlash", "error");
        } else {
            redirectAttributes.addFlashAttribute("mensagem", "Evento registrado com sucesso para " + pet.nome() + ".");
            redirectAttributes.addFlashAttribute("tipoFlash", "success");
        }

        return "redirect:/tutor/pets/" + form.getPetId();
    }

    @PostMapping("/pets/{id}/desativar")
    public String desativarPet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Mesma checagem de posse usada nas outras acoes: confirma que o
        // pet pertence ao tutor logado antes de desativar.
        PetResponse pet = buscarPetDoTutorLogado(id);

        petService.desativar(id);

        redirectAttributes.addFlashAttribute("mensagem",
                pet.nome() + " foi removido e não aparece mais na lista. O histórico dele continua preservado.");
        redirectAttributes.addFlashAttribute("tipoFlash", "success");

        return "redirect:/tutor/inicio";
    }

    @PostMapping("/eventos/{id}/excluir")
    public String excluirEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Confirma que o evento pertence a um pet do tutor logado antes de
        // excluir - sem essa checagem, bastaria adivinhar um id de evento
        // na URL para apagar o registro de outro tutor.
        EventoResponse evento = eventoService.buscarPorId(id);
        PetResponse pet = buscarPetDoTutorLogado(evento.petId());

        eventoService.excluir(id);

        redirectAttributes.addFlashAttribute("mensagem", "Evento removido da linha do tempo de " + pet.nome() + ".");
        redirectAttributes.addFlashAttribute("tipoFlash", "success");

        return "redirect:/tutor/pets/" + pet.id();
    }

    // ===== helpers =====

    private PetResponse buscarPetDoTutorLogado(Long petId) {
        Long tutorId = usuarioLogado.obter().getTutorId();
        PetResponse pet = petService.buscarPorId(petId);
        if (!pet.tutorId().equals(tutorId)) {
            throw new RecursoNaoEncontradoException("Pet", petId);
        }
        return pet;
    }

    private int contarAlertasAtivos(Long petId) {
        return (int) alertaRepository.findByPetId(petId, PageRequest.of(0, 100)).getContent()
                .stream().filter(a -> !Boolean.TRUE.equals(a.getResolvido())).count();
    }

    private String calcularStatus(Long petId) {
        boolean vermelho = alertaRepository.findByPetId(petId, PageRequest.of(0, 50)).getContent().stream()
                .anyMatch(a -> !a.getResolvido() && a.getNivel() == NivelAlerta.VERMELHO);
        if (vermelho) return "vermelho";

        boolean amarelo = alertaRepository.findByPetId(petId, PageRequest.of(0, 50)).getContent().stream()
                .anyMatch(a -> !a.getResolvido() && a.getNivel() == NivelAlerta.AMARELO);
        if (amarelo) return "amarelo";

        return "ok";
    }
}

package br.com.fiap.solin.web.vet;

import br.com.fiap.solin.dto.response.AlertaResponse;
import br.com.fiap.solin.dto.response.EventoResponse;
import br.com.fiap.solin.dto.response.PetResponse;
import br.com.fiap.solin.service.AlertaService;
import br.com.fiap.solin.service.EventoService;
import br.com.fiap.solin.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

/**
 * Fluxo completo do VETERINARIO (perfil ROLE_VETERINARIO), representando
 * a equipe clinica da Clyvo Vet:
 *
 *  1) Ver o painel consolidado de alertas de TODOS os pets do sistema,
 *     ordenados por gravidade (vermelho primeiro)
 *  2) Resolver um alerta apos o atendimento, tirando-o da lista de
 *     pendencias
 *  3) Abrir a ficha de qualquer pet (linha do tempo de eventos +
 *     alertas) a partir de "Todos os pets"
 *  4) Excluir um evento incorreto/duplicado da linha do tempo de
 *     qualquer pet
 *
 * Diferente da area do tutor, aqui nao ha filtro por dono: o
 * veterinario precisa enxergar e gerenciar todos os pacientes da
 * clinica, nao so os de um tutor especifico.
 */
@Controller
@RequestMapping("/vet")
@PreAuthorize("hasRole('VETERINARIO')")
@RequiredArgsConstructor
public class VeterinarioAreaController {

    private final AlertaService alertaService;
    private final PetService petService;
    private final EventoService eventoService;

    @GetMapping("/painel")
    public String painel(@RequestParam(required = false) Boolean resolvido, Model model) {
        List<AlertaResponse> alertas = alertaService.listarParaPainel(resolvido);

        // Vermelho primeiro, depois amarelo; dentro de cada nivel, o mais recente primeiro.
        List<AlertaResponse> ordenados = alertas.stream()
                .sorted(Comparator
                        .comparing((AlertaResponse a) -> a.nivel().name().equals("VERMELHO") ? 0 : 1)
                        .thenComparing(AlertaResponse::dataHoraGerado, Comparator.reverseOrder()))
                .toList();

        long pendentes = alertas.stream().filter(a -> !Boolean.TRUE.equals(a.resolvido())).count();
        long criticos = alertas.stream()
                .filter(a -> !Boolean.TRUE.equals(a.resolvido()) && a.nivel().name().equals("VERMELHO"))
                .count();

        model.addAttribute("alertas", ordenados);
        model.addAttribute("filtroResolvido", resolvido);
        model.addAttribute("totalPendentes", pendentes);
        model.addAttribute("totalCriticos", criticos);
        return "vet/painel";
    }

    @PostMapping("/alertas/{id}/resolver")
    public String resolverAlerta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AlertaResponse alerta = alertaService.marcarComoResolvido(id);
        redirectAttributes.addFlashAttribute("mensagem",
                "Alerta de " + alerta.nomePet() + " marcado como resolvido.");
        redirectAttributes.addFlashAttribute("tipoFlash", "success");
        return "redirect:/vet/painel";
    }

    @GetMapping("/pets")
    public String todosOsPets(Model model) {
        // Mesma regra da tela do tutor: pets desativados (soft delete)
        // nao aparecem na listagem do dia a dia da clinica.
        List<PetResponse> pets = petService
                .listarAtivos(null, null, null, PageRequest.of(0, 200, Sort.by("nome")))
                .getContent();
        model.addAttribute("pets", pets);
        return "vet/pets";
    }

    @GetMapping("/pets/{id}")
    @Transactional(readOnly = true)
    public String detalhePet(@PathVariable Long id, Model model) {
        PetResponse pet = petService.buscarPorId(id);

        List<EventoResponse> eventos = eventoService
                .listarPorPet(id, null, null, null, PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "dataHora")))
                .getContent();

        List<AlertaResponse> alertas = alertaService
                .listarPorPet(id, null, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataHoraGerado")))
                .getContent();

        model.addAttribute("pet", pet);
        model.addAttribute("eventos", eventos);
        model.addAttribute("alertas", alertas);
        return "vet/pet-detalhe";
    }

    @PostMapping("/pets/{petId}/eventos/{id}/excluir")
    public String excluirEvento(@PathVariable Long petId, @PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        // Diferente do tutor, o veterinario pode excluir evento de
        // qualquer pet da clinica - nao ha checagem de "dono" aqui,
        // pois o perfil ROLE_VETERINARIO enxerga todos os pacientes.
        eventoService.excluir(id);

        redirectAttributes.addFlashAttribute("mensagem", "Evento removido da linha do tempo.");
        redirectAttributes.addFlashAttribute("tipoFlash", "success");

        return "redirect:/vet/pets/" + petId;
    }
}

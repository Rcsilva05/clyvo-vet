package br.com.fiap.solin.web.tutor;

import br.com.fiap.solin.dto.response.PetResponse;
import br.com.fiap.solin.exception.RecursoNaoEncontradoException;
import br.com.fiap.solin.security.UsuarioLogado;
import br.com.fiap.solin.service.AssistenteService;
import br.com.fiap.solin.service.PetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * "Assistente SOLIN": chat de orientacao para o tutor, com respostas
 * geradas por regras de negocio (AssistenteService) a partir dos dados
 * reais do pet - NAO e uma integracao com IA generativa/LLM externa.
 *
 * Essa escolha e deliberada (ver AssistenteService para a justificativa
 * completa): sem chave de API, sem dependencia de servico de terceiros,
 * comportamento 100% previsivel e facil de demonstrar/explicar.
 *
 * Segue o mesmo padrao de seguranca do restante da area do tutor: toda
 * pergunta e resolvida em cima de um pet que precisa pertencer ao tutor
 * autenticado (ver buscarPetDoTutorLogado).
 */
@Controller
@RequestMapping("/tutor/assistente")
@PreAuthorize("hasRole('TUTOR')")
@RequiredArgsConstructor
public class AssistenteController {

    private final AssistenteService assistenteService;
    private final PetService petService;
    private final UsuarioLogado usuarioLogado;

    @GetMapping
    public String abrir(@RequestParam(required = false) Long petId, Model model) {
        Long tutorId = usuarioLogado.obter().getTutorId();
        List<PetResponse> pets = petService
                .listarAtivosPorTutor(tutorId, PageRequest.of(0, 50, Sort.by("nome")))
                .getContent();

        Long petSelecionadoId = petId;
        if (petSelecionadoId == null && !pets.isEmpty()) {
            petSelecionadoId = pets.get(0).id();
        }

        model.addAttribute("pets", pets);
        model.addAttribute("petSelecionadoId", petSelecionadoId);
        model.addAttribute("perguntas", assistenteService.perguntasSugeridas());
        return "tutor/assistente";
    }

    @PostMapping("/perguntar")
    @ResponseBody
    public RespostaChat perguntar(@RequestBody PerguntaChat requisicao) {
        PetResponse pet = buscarPetDoTutorLogado(requisicao.getPetId());
        String resposta = assistenteService.responder(petService.buscarEntidade(pet.id()), requisicao.getPergunta());
        return new RespostaChat(resposta);
    }

    private PetResponse buscarPetDoTutorLogado(Long petId) {
        Long tutorId = usuarioLogado.obter().getTutorId();
        PetResponse pet = petService.buscarPorId(petId);
        if (!pet.tutorId().equals(tutorId)) {
            throw new RecursoNaoEncontradoException("Pet", petId);
        }
        return pet;
    }

    /**
     * Tratamento de erro proprio para o endpoint /perguntar: como ele e
     * @ResponseBody (o JavaScript do chat espera JSON), nao pode cair no
     * WebExceptionHandler generico do pacote "web" (que redireciona para
     * a pagina HTML "erro") - isso quebraria o fetch() no front. Este
     * handler tem prioridade sobre o @ControllerAdvice mais generico por
     * estar declarado direto na classe do controller.
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public RespostaChat tratarPetNaoEncontrado(RecursoNaoEncontradoException ex) {
        return new RespostaChat("Não encontrei esse pet na sua conta. Atualize a página e tente novamente.");
    }

    @Data
    public static class PerguntaChat {
        private Long petId;
        private String pergunta;
    }

    public record RespostaChat(String resposta) {}
}

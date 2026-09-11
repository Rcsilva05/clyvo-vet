package br.com.fiap.solin.service;

import br.com.fiap.solin.entity.Alerta;
import br.com.fiap.solin.entity.Evento;
import br.com.fiap.solin.entity.Pet;
import br.com.fiap.solin.enums.NivelAlerta;
import br.com.fiap.solin.enums.TipoEvento;
import br.com.fiap.solin.repository.AlertaRepository;
import br.com.fiap.solin.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * "Assistente SOLIN": um chat de orientacao para o tutor, com respostas
 * geradas por REGRAS de negocio em cima dos dados reais do pet (eventos
 * e alertas ja existentes no banco) - NAO e uma integracao com IA
 * generativa externa (tipo ChatGPT/Gemini).
 *
 * Essa escolha foi deliberada: um assistente baseado em regras e 100%
 * previsivel, nao depende de internet/chave de API de terceiros, e o
 * comportamento inteiro pode ser explicado e demonstrado linha a linha
 * na defesa do projeto. Ele reaproveita exatamente os mesmos dados que
 * ja alimentam o motor de alertas (Strategy) - nao ha nenhum calculo
 * novo, so uma forma diferente (conversacional) de apresentar
 * informacao que o sistema ja tem.
 */
@Service
@RequiredArgsConstructor
public class AssistenteService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");

    private final EventoRepository eventoRepository;
    private final AlertaRepository alertaRepository;

    /**
     * Perguntas prontas que aparecem como botoes no chat. O texto exato
     * de cada uma e usado tanto para exibir o botao quanto para escolher
     * qual resposta gerar (ver responder()).
     */
    public List<String> perguntasSugeridas() {
        return List.of(
                "Como está a saúde do pet hoje?",
                "Meu pet está com alerta, o que eu faço?",
                "Quando devo levar meu pet à clínica?",
                "Quais eventos já registrei recentemente?"
        );
    }

    @Transactional(readOnly = true)
    public String responder(Pet pet, String pergunta) {
        return switch (pergunta) {
            case "Como está a saúde do pet hoje?" -> resumoSaude(pet);
            case "Meu pet está com alerta, o que eu faço?" -> orientacaoAlerta(pet);
            case "Quando devo levar meu pet à clínica?" -> quandoProcurarClinica(pet);
            case "Quais eventos já registrei recentemente?" -> ultimosEventos(pet);
            default -> "Não entendi essa pergunta. Escolha uma das opções sugeridas abaixo, ou registre um "
                    + "evento na ficha do pet para eu conseguir avaliar a rotina dele.";
        };
    }

    // ===== regras de cada resposta =====

    private String resumoSaude(Pet pet) {
        Optional<Alerta> piorAlertaAtivo = alertaAtivoMaisGrave(pet.getId());

        StringBuilder texto = new StringBuilder();
        if (piorAlertaAtivo.isEmpty()) {
            texto.append("Boa notícia: ").append(pet.getNome()).append(" está com a rotina normal, sem nenhum alerta ativo no momento.\n\n");
        } else {
            Alerta alerta = piorAlertaAtivo.get();
            texto.append(alerta.getNivel() == NivelAlerta.VERMELHO ? "⚠️ " : "🟡 ")
                    .append(pet.getNome()).append(" está com um alerta ")
                    .append(alerta.getNivel() == NivelAlerta.VERMELHO ? "crítico" : "de atenção")
                    .append(" ativo: \"").append(alerta.getMensagem()).append("\"\n\n");
        }

        texto.append("Últimos registros:\n");
        texto.append(linhaUltimoEvento(pet, TipoEvento.URINOU, "Urina"));
        texto.append(linhaUltimoEvento(pet, TipoEvento.BEBEU_AGUA, "Água"));
        texto.append(linhaUltimoEvento(pet, TipoEvento.COMEU, "Alimentação"));

        return texto.toString();
    }

    private String orientacaoAlerta(Pet pet) {
        Optional<Alerta> piorAlertaAtivo = alertaAtivoMaisGrave(pet.getId());

        if (piorAlertaAtivo.isEmpty()) {
            return pet.getNome() + " não está com nenhum alerta ativo agora, então não há necessidade de nenhuma "
                    + "ação imediata. Continue registrando a rotina normalmente pelo SOLIN.";
        }

        Alerta alerta = piorAlertaAtivo.get();
        if (alerta.getNivel() == NivelAlerta.VERMELHO) {
            return "⚠️ Esse é um alerta CRÍTICO: \"" + alerta.getMensagem() + "\"\n\n"
                    + "Recomendação: entre em contato com a Clyvo Vet o quanto antes ou procure atendimento "
                    + "veterinário presencial. Alertas vermelhos indicam que o tempo sem o registro esperado já "
                    + "passou do dobro do limite normal para a espécie de " + pet.getNome() + ".";
        }

        return "🟡 Esse é um alerta de ATENÇÃO: \"" + alerta.getMensagem() + "\"\n\n"
                + "Recomendação: observe " + pet.getNome() + " de perto nas próximas horas e registre um novo "
                + "evento assim que possível. Se o alerta virar crítico (vermelho) ou você notar outros sinais "
                + "incomuns, contate a clínica.";
    }

    private String quandoProcurarClinica(Pet pet) {
        Optional<Alerta> piorAlertaAtivo = alertaAtivoMaisGrave(pet.getId());

        String base = "De forma geral, procure a Clyvo Vet imediatamente sempre que "
                + pet.getNome() + " tiver um alerta CRÍTICO (vermelho) ativo, ou se notar sinais como recusa "
                + "total de água/comida, letargia incomum ou qualquer mudança brusca de comportamento — mesmo "
                + "sem alerta disparado, essas mudanças merecem avaliação profissional.";

        if (piorAlertaAtivo.isPresent() && piorAlertaAtivo.get().getNivel() == NivelAlerta.VERMELHO) {
            return "⚠️ Com base no histórico atual, " + pet.getNome() + " JÁ está em situação crítica agora: \""
                    + piorAlertaAtivo.get().getMensagem() + "\" — recomendo procurar a clínica hoje mesmo.\n\n" + base;
        }

        return base;
    }

    private String ultimosEventos(Pet pet) {
        List<Evento> eventos = eventoRepository
                .findByPetId(pet.getId(), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dataHora")))
                .getContent();

        if (eventos.isEmpty()) {
            return "Ainda não há nenhum evento registrado para " + pet.getNome()
                    + ". Use \"Registrar evento\" no menu para começar o acompanhamento.";
        }

        StringBuilder texto = new StringBuilder("Últimos eventos registrados para " + pet.getNome() + ":\n\n");
        for (Evento evento : eventos) {
            texto.append("• ").append(evento.getTipo()).append(" — ")
                    .append(evento.getDataHora().format(FORMATO_DATA)).append("\n");
        }
        return texto.toString();
    }

    // ===== helpers =====

    private Optional<Alerta> alertaAtivoMaisGrave(Long petId) {
        List<Alerta> alertasAtivos = alertaRepository.findByPetId(petId, PageRequest.of(0, 50)).getContent()
                .stream().filter(a -> !Boolean.TRUE.equals(a.getResolvido())).toList();

        return alertasAtivos.stream().filter(a -> a.getNivel() == NivelAlerta.VERMELHO).findFirst()
                .or(() -> alertasAtivos.stream().filter(a -> a.getNivel() == NivelAlerta.AMARELO).findFirst());
    }

    private String linhaUltimoEvento(Pet pet, TipoEvento tipo, String rotulo) {
        Optional<Evento> ultimo = eventoRepository.findFirstByPetIdAndTipoOrderByDataHoraDesc(pet.getId(), tipo);

        if (ultimo.isEmpty()) {
            return "• " + rotulo + ": nenhum registro ainda\n";
        }

        long horas = Duration.between(ultimo.get().getDataHora(), LocalDateTime.now()).toHours();
        return "• " + rotulo + ": há " + horas + "h (" + ultimo.get().getDataHora().format(FORMATO_DATA) + ")\n";
    }
}

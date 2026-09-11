package br.com.fiap.solin.strategy;

import br.com.fiap.solin.entity.Alerta;
import br.com.fiap.solin.entity.Evento;
import br.com.fiap.solin.entity.Pet;
import br.com.fiap.solin.enums.NivelAlerta;
import br.com.fiap.solin.enums.TipoEvento;
import br.com.fiap.solin.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Segunda regra de alerta do SOLIN (alem da de urina): hidratacao.
 *
 * Dispara alerta AMARELO quando o pet passa do intervalo "normal" de
 * horas sem beber agua da sua especie, e ainda nao chegou ao dobro
 * (que seria VERMELHO). Mesma logica da RegraSemUrinarAmarelo, so que
 * usando TipoEvento.BEBEU_AGUA e o limite QT_HORAS_MAX_AGUA da especie.
 *
 * Por ser mais uma implementacao de RegraAlertaStrategy, o Spring ja
 * injeta ela automaticamente na lista de regras do AlertaService - nao
 * precisou alterar nenhuma classe existente para o motor de alertas
 * passar a considerar hidratacao tambem (Open/Closed Principle).
 */
@Component
@RequiredArgsConstructor
public class RegraSemBeberAguaAmarelo implements RegraAlertaStrategy {

    private final EventoRepository eventoRepository;

    @Override
    public Optional<Alerta> avaliar(Pet pet) {
        int limiteHoras = pet.getEspecie().getHorasMaximasSemBeberAgua();

        Optional<Evento> ultimaAgua = eventoRepository
                .findFirstByPetIdAndTipoOrderByDataHoraDesc(pet.getId(), TipoEvento.BEBEU_AGUA);

        // se nunca registrou, nao dispara (vai disparar so quando houver historico)
        if (ultimaAgua.isEmpty()) {
            return Optional.empty();
        }

        long horasDesdeUltima = Duration.between(
                ultimaAgua.get().getDataHora(), LocalDateTime.now()
        ).toHours();

        boolean dentroDaJanelaAmarela =
                horasDesdeUltima >= limiteHoras && horasDesdeUltima < (limiteHoras * 2L);

        if (!dentroDaJanelaAmarela) {
            return Optional.empty();
        }

        Alerta alerta = Alerta.builder()
                .pet(pet)
                .nivel(NivelAlerta.AMARELO)
                .mensagem(String.format(
                        "O pet %s esta ha %d horas sem beber agua (limite normal: %dh).",
                        pet.getNome(), horasDesdeUltima, limiteHoras))
                .dataHoraGerado(LocalDateTime.now())
                .resolvido(false)
                .build();

        return Optional.of(alerta);
    }
}

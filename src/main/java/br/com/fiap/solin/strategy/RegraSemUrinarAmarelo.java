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
 * Dispara alerta AMARELO quando o pet passa do intervalo "normal"
 * da especie e ainda nao chegou ao dobro (que seria VERMELHO).
 *
 * Ex: para um cao com 8h como limite normal, dispara entre 8h e 16h
 * sem registro de urina.
 */
@Component
@RequiredArgsConstructor
public class RegraSemUrinarAmarelo implements RegraAlertaStrategy {

    private final EventoRepository eventoRepository;

    @Override
    public Optional<Alerta> avaliar(Pet pet) {
        int limiteHoras = pet.getEspecie().getHorasMaximasSemUrinar();

        Optional<Evento> ultimaUrina = eventoRepository
                .findFirstByPetIdAndTipoOrderByDataHoraDesc(pet.getId(), TipoEvento.URINOU);

        // se nunca registrou, nao dispara (vai dispar so quando houver historico)
        if (ultimaUrina.isEmpty()) {
            return Optional.empty();
        }

        long horasDesdeUltima = Duration.between(
                ultimaUrina.get().getDataHora(), LocalDateTime.now()
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
                        "O pet %s esta ha %d horas sem registro de urina (limite normal: %dh).",
                        pet.getNome(), horasDesdeUltima, limiteHoras))
                .dataHoraGerado(LocalDateTime.now())
                .resolvido(false)
                .build();

        return Optional.of(alerta);
    }
}

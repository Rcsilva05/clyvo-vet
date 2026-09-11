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
 * Dispara alerta VERMELHO quando o pet passa do DOBRO do intervalo
 * normal sem urinar. Situacao critica que deve ser comunicada
 * imediatamente ao veterinario.
 */
@Component
@RequiredArgsConstructor
public class RegraSemUrinarVermelho implements RegraAlertaStrategy {

    private final EventoRepository eventoRepository;

    @Override
    public Optional<Alerta> avaliar(Pet pet) {
        int limiteHoras = pet.getEspecie().getHorasMaximasSemUrinar();
        long limiteCritico = limiteHoras * 2L;

        Optional<Evento> ultimaUrina = eventoRepository
                .findFirstByPetIdAndTipoOrderByDataHoraDesc(pet.getId(), TipoEvento.URINOU);

        if (ultimaUrina.isEmpty()) {
            return Optional.empty();
        }

        long horasDesdeUltima = Duration.between(
                ultimaUrina.get().getDataHora(), LocalDateTime.now()
        ).toHours();

        if (horasDesdeUltima < limiteCritico) {
            return Optional.empty();
        }

        Alerta alerta = Alerta.builder()
                .pet(pet)
                .nivel(NivelAlerta.VERMELHO)
                .mensagem(String.format(
                        "URGENTE: o pet %s esta ha %d horas sem urinar. " +
                        "Contate o veterinario.",
                        pet.getNome(), horasDesdeUltima))
                .dataHoraGerado(LocalDateTime.now())
                .resolvido(false)
                .build();

        return Optional.of(alerta);
    }
}

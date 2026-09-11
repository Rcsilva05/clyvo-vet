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
 * normal sem beber agua. Situacao critica de desidratacao que deve
 * ser comunicada imediatamente ao veterinario. Espelha exatamente a
 * RegraSemUrinarVermelho, trocando apenas o tipo de evento e o limite
 * de horas usados (agua em vez de urina).
 */
@Component
@RequiredArgsConstructor
public class RegraSemBeberAguaVermelho implements RegraAlertaStrategy {

    private final EventoRepository eventoRepository;

    @Override
    public Optional<Alerta> avaliar(Pet pet) {
        int limiteHoras = pet.getEspecie().getHorasMaximasSemBeberAgua();
        long limiteCritico = limiteHoras * 2L;

        Optional<Evento> ultimaAgua = eventoRepository
                .findFirstByPetIdAndTipoOrderByDataHoraDesc(pet.getId(), TipoEvento.BEBEU_AGUA);

        if (ultimaAgua.isEmpty()) {
            return Optional.empty();
        }

        long horasDesdeUltima = Duration.between(
                ultimaAgua.get().getDataHora(), LocalDateTime.now()
        ).toHours();

        if (horasDesdeUltima < limiteCritico) {
            return Optional.empty();
        }

        Alerta alerta = Alerta.builder()
                .pet(pet)
                .nivel(NivelAlerta.VERMELHO)
                .mensagem(String.format(
                        "URGENTE: o pet %s esta ha %d horas sem beber agua. " +
                        "Risco de desidratacao - contate o veterinario.",
                        pet.getNome(), horasDesdeUltima))
                .dataHoraGerado(LocalDateTime.now())
                .resolvido(false)
                .build();

        return Optional.of(alerta);
    }
}

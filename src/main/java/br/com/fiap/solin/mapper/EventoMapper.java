package br.com.fiap.solin.mapper;

import br.com.fiap.solin.dto.request.EventoRequest;
import br.com.fiap.solin.dto.response.EventoResponse;
import br.com.fiap.solin.entity.Evento;
import br.com.fiap.solin.entity.Pet;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public Evento toEntity(EventoRequest dto, Pet pet) {
        return Evento.builder()
                .tipo(dto.tipo())
                .dataHora(dto.dataHora())
                .origem(dto.origem())
                .observacao(dto.observacao())
                .pet(pet)
                .build();
    }

    public EventoResponse toResponse(Evento evento) {
        return new EventoResponse(
                evento.getId(),
                evento.getTipo(),
                evento.getDataHora(),
                evento.getOrigem(),
                evento.getObservacao(),
                evento.getPet().getId(),
                evento.getPet().getNome()
        );
    }
}

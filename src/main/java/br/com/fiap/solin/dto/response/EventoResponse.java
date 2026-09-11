package br.com.fiap.solin.dto.response;

import br.com.fiap.solin.enums.OrigemEvento;
import br.com.fiap.solin.enums.TipoEvento;

import java.time.LocalDateTime;

public record EventoResponse(
        Long id,
        TipoEvento tipo,
        LocalDateTime dataHora,
        OrigemEvento origem,
        String observacao,
        Long petId,
        String nomePet
) {}

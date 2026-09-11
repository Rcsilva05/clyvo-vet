package br.com.fiap.solin.dto.request;

import br.com.fiap.solin.enums.OrigemEvento;
import br.com.fiap.solin.enums.TipoEvento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventoRequest(

        @NotNull(message = "O tipo do evento e obrigatorio")
        TipoEvento tipo,

        @NotNull(message = "A data e hora do evento sao obrigatorias")
        @PastOrPresent(message = "A data do evento nao pode estar no futuro")
        LocalDateTime dataHora,

        @NotNull(message = "A origem do evento e obrigatoria")
        OrigemEvento origem,

        @Size(max = 255, message = "A observacao deve ter no maximo 255 caracteres")
        String observacao,

        @NotNull(message = "O id do pet e obrigatorio")
        Long petId
) {}

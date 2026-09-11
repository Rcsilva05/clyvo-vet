package br.com.fiap.solin.dto.response;

import br.com.fiap.solin.enums.NivelAlerta;

import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        NivelAlerta nivel,
        String mensagem,
        LocalDateTime dataHoraGerado,
        Boolean resolvido,
        Long petId,
        String nomePet
) {}

package br.com.fiap.solin.dto.response;

import java.time.LocalDate;

public record TutorResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalDate dataCadastro,
        Integer quantidadePets
) {}

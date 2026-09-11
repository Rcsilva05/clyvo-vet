package br.com.fiap.solin.dto.response;

import br.com.fiap.solin.enums.Sexo;

import java.time.LocalDate;

public record PetResponse(
        Long id,
        String nome,
        String raca,
        LocalDate dataNascimento,
        Double pesoKg,
        Sexo sexo,
        Long tutorId,
        String nomeTutor,
        Long especieId,
        String nomeEspecie,
        Boolean ativo
) {}

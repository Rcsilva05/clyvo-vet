package br.com.fiap.solin.dto.request;

import br.com.fiap.solin.enums.Sexo;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PetRequest(

        @NotBlank(message = "O nome do pet e obrigatorio")
        @Size(min = 1, max = 80)
        String nome,

        @Size(max = 80)
        String raca,

        @PastOrPresent(message = "A data de nascimento nao pode estar no futuro")
        LocalDate dataNascimento,

        @DecimalMin(value = "0.1", message = "O peso deve ser maior que 0,1 kg")
        @DecimalMax(value = "150.0", message = "O peso parece irreal")
        Double pesoKg,

        Sexo sexo,

        @NotNull(message = "O id do tutor e obrigatorio")
        Long tutorId,

        @NotNull(message = "O id da especie e obrigatorio")
        Long especieId
) {}

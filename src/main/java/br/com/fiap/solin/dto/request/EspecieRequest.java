package br.com.fiap.solin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EspecieRequest(

        @NotBlank(message = "O nome da especie e obrigatorio")
        String nome,

        @NotNull(message = "Informe o intervalo maximo de horas sem urinar")
        @Min(value = 1, message = "O intervalo minimo e 1 hora")
        @Max(value = 72, message = "O intervalo maximo e 72 horas")
        Integer horasMaximasSemUrinar,

        @NotNull(message = "Informe o intervalo maximo de horas sem beber agua")
        @Min(value = 1, message = "O intervalo minimo e 1 hora")
        @Max(value = 72, message = "O intervalo maximo e 72 horas")
        Integer horasMaximasSemBeberAgua
) {}

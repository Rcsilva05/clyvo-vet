package br.com.fiap.solin.dto.response;

public record EspecieResponse(
        Long id,
        String nome,
        Integer horasMaximasSemUrinar,
        Integer horasMaximasSemBeberAgua
) {}

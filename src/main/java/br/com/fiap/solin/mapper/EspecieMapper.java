package br.com.fiap.solin.mapper;

import br.com.fiap.solin.dto.request.EspecieRequest;
import br.com.fiap.solin.dto.response.EspecieResponse;
import br.com.fiap.solin.entity.Especie;
import org.springframework.stereotype.Component;

@Component
public class EspecieMapper {

    public Especie toEntity(EspecieRequest dto) {
        return Especie.builder()
                .nome(dto.nome())
                .horasMaximasSemUrinar(dto.horasMaximasSemUrinar())
                .horasMaximasSemBeberAgua(dto.horasMaximasSemBeberAgua())
                .build();
    }

    public EspecieResponse toResponse(Especie especie) {
        return new EspecieResponse(
                especie.getId(),
                especie.getNome(),
                especie.getHorasMaximasSemUrinar(),
                especie.getHorasMaximasSemBeberAgua()
        );
    }
}

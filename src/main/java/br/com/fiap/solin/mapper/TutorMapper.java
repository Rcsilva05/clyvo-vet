package br.com.fiap.solin.mapper;

import br.com.fiap.solin.dto.request.TutorRequest;
import br.com.fiap.solin.dto.response.TutorResponse;
import br.com.fiap.solin.entity.Tutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TutorMapper {

    public Tutor toEntity(TutorRequest dto) {
        return Tutor.builder()
                .nome(dto.nome())
                .email(dto.email())
                .telefone(dto.telefone())
                .dataCadastro(LocalDate.now())
                .build();
    }

    public TutorResponse toResponse(Tutor tutor) {
        return new TutorResponse(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail(),
                tutor.getTelefone(),
                tutor.getDataCadastro(),
                tutor.getPets() == null ? 0 : tutor.getPets().size()
        );
    }
}

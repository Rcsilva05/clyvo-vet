package br.com.fiap.solin.mapper;

import br.com.fiap.solin.dto.request.PetRequest;
import br.com.fiap.solin.dto.response.PetResponse;
import br.com.fiap.solin.entity.Especie;
import br.com.fiap.solin.entity.Pet;
import br.com.fiap.solin.entity.Tutor;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public Pet toEntity(PetRequest dto, Tutor tutor, Especie especie) {
        return Pet.builder()
                .nome(dto.nome())
                .raca(dto.raca())
                .dataNascimento(dto.dataNascimento())
                .pesoKg(dto.pesoKg())
                .sexo(dto.sexo())
                .tutor(tutor)
                .especie(especie)
                .build();
    }

    public PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getNome(),
                pet.getRaca(),
                pet.getDataNascimento(),
                pet.getPesoKg(),
                pet.getSexo(),
                pet.getTutor().getId(),
                pet.getTutor().getNome(),
                pet.getEspecie().getId(),
                pet.getEspecie().getNome(),
                pet.getAtivo()
        );
    }
}

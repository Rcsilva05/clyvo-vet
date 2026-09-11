package br.com.fiap.solin.web.tutor.form;

import br.com.fiap.solin.dto.request.PetRequest;
import br.com.fiap.solin.enums.Sexo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Objeto de formulario (nao e a entidade nem o DTO de API) usado pela
 * tela "Cadastrar pet". O tutorId nao vem do formulario: e sempre
 * preenchido pelo controller com o id do tutor autenticado, para que
 * um tutor jamais consiga cadastrar um pet em nome de outro tutor.
 *
 * O formulario pede "idade aproximada em anos" em vez da data exata de
 * nascimento: na pratica, a maioria dos tutores nao sabe a data exata
 * (especialmente em pets resgatados), entao pedir isso obrigatoriamente
 * so atrapalharia o cadastro. A idade e opcional e, quando informada, e
 * convertida para uma data de nascimento aproximada (dia 1 de janeiro
 * do ano correspondente) apenas para preencher o campo que a entidade
 * Pet ja possui.
 */
@Data
public class PetForm {

    @NotBlank(message = "Informe o nome do pet")
    @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
    private String nome;

    @Size(max = 80, message = "A raça deve ter no máximo 80 caracteres")
    private String raca;

    @Min(value = 0, message = "A idade não pode ser negativa")
    @Max(value = 40, message = "Confira a idade informada")
    private Integer idadeAnos;

    @NotNull(message = "Informe o peso do pet")
    @DecimalMin(value = "0.1", message = "O peso deve ser maior que 0,1 kg")
    @DecimalMax(value = "150.0", message = "Esse peso parece irreal, confira o valor")
    private Double pesoKg;

    @NotNull(message = "Selecione o sexo do pet")
    private Sexo sexo;

    @NotNull(message = "Selecione a espécie do pet")
    private Long especieId;

    public PetRequest paraRequest(Long tutorId) {
        LocalDate dataNascimentoAproximada = (idadeAnos != null)
                ? LocalDate.of(LocalDate.now().getYear() - idadeAnos, 1, 1)
                : null;
        return new PetRequest(nome, raca, dataNascimentoAproximada, pesoKg, sexo, tutorId, especieId);
    }
}

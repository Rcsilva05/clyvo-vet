package br.com.fiap.solin.web.tutor.form;

import br.com.fiap.solin.dto.request.EventoRequest;
import br.com.fiap.solin.enums.OrigemEvento;
import br.com.fiap.solin.enums.TipoEvento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Objeto de formulario (nao e a entidade nem o DTO de API) usado pela
 * tela "Registrar evento". Existe para dar suporte a data/hora vindas
 * como texto do <input type="datetime-local"> e para validar os campos
 * antes de converter para o EventoRequest que o service ja entende.
 */
@Data
public class EventoForm {

    @NotNull(message = "Selecione o pet")
    private Long petId;

    @NotNull(message = "Selecione o tipo de evento")
    private TipoEvento tipo;

    @NotNull(message = "Informe a data e hora do evento")
    @PastOrPresent(message = "A data do evento não pode estar no futuro")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataHora = LocalDateTime.now();

    @NotNull(message = "Selecione a origem do registro")
    private OrigemEvento origem = OrigemEvento.MANUAL;

    @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
    private String observacao;

    public EventoRequest paraRequest() {
        return new EventoRequest(tipo, dataHora, origem, observacao, petId);
    }
}

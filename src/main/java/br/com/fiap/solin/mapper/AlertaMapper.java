package br.com.fiap.solin.mapper;

import br.com.fiap.solin.dto.response.AlertaResponse;
import br.com.fiap.solin.entity.Alerta;
import org.springframework.stereotype.Component;

@Component
public class AlertaMapper {

    public AlertaResponse toResponse(Alerta alerta) {
        return new AlertaResponse(
                alerta.getId(),
                alerta.getNivel(),
                alerta.getMensagem(),
                alerta.getDataHoraGerado(),
                alerta.getResolvido(),
                alerta.getPet().getId(),
                alerta.getPet().getNome()
        );
    }
}

package br.com.fiap.solin.controller;

import br.com.fiap.solin.dto.response.AlertaResponse;
import br.com.fiap.solin.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Alertas gerados automaticamente pelas regras de saude")
public class AlertaController {

    private final AlertaService service;

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar alertas de um pet, paginados, com filtro opcional por situacao")
    public ResponseEntity<Page<AlertaResponse>> listarPorPet(
            @PathVariable Long petId,
            @Parameter(description = "true = apenas resolvidos, false = apenas pendentes")
                @RequestParam(required = false) Boolean resolvido,
            @ParameterObject @PageableDefault(size = 10, sort = "dataHoraGerado") Pageable pageable) {
        return ResponseEntity.ok(service.listarPorPet(petId, resolvido, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar alerta por id")
    public ResponseEntity<AlertaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/resolver")
    @Operation(summary = "Marcar alerta como resolvido")
    public ResponseEntity<AlertaResponse> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarComoResolvido(id));
    }
}

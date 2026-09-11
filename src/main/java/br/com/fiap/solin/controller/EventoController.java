package br.com.fiap.solin.controller;

import br.com.fiap.solin.dto.request.EventoRequest;
import br.com.fiap.solin.dto.response.EventoResponse;
import br.com.fiap.solin.enums.TipoEvento;
import br.com.fiap.solin.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Registro diario de eventos do pet (urinou, comeu, etc.)")
public class EventoController {

    private final EventoService service;

    @PostMapping
    @Operation(summary = "Registrar novo evento",
               description = "Apos registrar, o sistema reavalia automaticamente as regras de alerta para o pet.")
    public ResponseEntity<EventoResponse> registrar(@Valid @RequestBody EventoRequest request) {
        EventoResponse criado = service.registrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar eventos de um pet com filtros e paginacao",
               description = "Filtre por tipo e por intervalo de datas. Ex: /api/eventos/pet/1?tipo=URINOU")
    public ResponseEntity<Page<EventoResponse>> listarPorPet(
            @PathVariable Long petId,
            @Parameter(description = "Tipo do evento") @RequestParam(required = false) TipoEvento tipo,
            @Parameter(description = "Inicio do intervalo (ISO: 2025-05-17T00:00:00)")
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fim do intervalo (ISO)")
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @ParameterObject @PageableDefault(size = 10, sort = "dataHora") Pageable pageable) {
        return ResponseEntity.ok(service.listarPorPet(petId, tipo, inicio, fim, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento por id")
    public ResponseEntity<EventoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir evento")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

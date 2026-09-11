package br.com.fiap.solin.controller;

import br.com.fiap.solin.dto.request.EspecieRequest;
import br.com.fiap.solin.dto.response.EspecieResponse;
import br.com.fiap.solin.service.EspecieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/especies")
@RequiredArgsConstructor
@Tag(name = "Especies", description = "Especies de pet (cao, gato, etc.) e seus limites de saude")
public class EspecieController {

    private final EspecieService service;

    @PostMapping
    @Operation(summary = "Cadastrar nova especie")
    public ResponseEntity<EspecieResponse> cadastrar(@Valid @RequestBody EspecieRequest request) {
        EspecieResponse criada = service.cadastrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @Operation(summary = "Listar especies paginadas")
    public ResponseEntity<Page<EspecieResponse>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar especie por id")
    public ResponseEntity<EspecieResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar especie")
    public ResponseEntity<EspecieResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody EspecieRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir especie")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

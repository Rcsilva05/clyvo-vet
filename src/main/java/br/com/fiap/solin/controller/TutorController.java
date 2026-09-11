package br.com.fiap.solin.controller;

import br.com.fiap.solin.dto.request.TutorRequest;
import br.com.fiap.solin.dto.response.TutorResponse;
import br.com.fiap.solin.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/tutores")
@RequiredArgsConstructor
@Tag(name = "Tutores", description = "Gerenciamento de tutores (donos de pets)")
public class TutorController {

    private final TutorService service;

    @PostMapping
    @Operation(summary = "Cadastrar novo tutor")
    public ResponseEntity<TutorResponse> cadastrar(@Valid @RequestBody TutorRequest request) {
        TutorResponse criado = service.cadastrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar tutores paginados, com busca opcional por nome")
    public ResponseEntity<Page<TutorResponse>> listar(
            @Parameter(description = "Filtra por trecho do nome") @RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tutor por id")
    public ResponseEntity<TutorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tutor")
    public ResponseEntity<TutorResponse> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody TutorRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tutor")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

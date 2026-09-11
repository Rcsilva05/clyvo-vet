package br.com.fiap.solin.controller;

import br.com.fiap.solin.dto.request.PetRequest;
import br.com.fiap.solin.dto.response.PetResponse;
import br.com.fiap.solin.service.PetService;
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
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Cadastro e gerenciamento dos pets monitorados")
public class PetController {

    private final PetService service;

    @PostMapping
    @Operation(summary = "Cadastrar novo pet")
    public ResponseEntity<PetResponse> cadastrar(@Valid @RequestBody PetRequest request) {
        PetResponse criado = service.cadastrar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar pets com filtros, paginacao e ordenacao",
               description = "Filtros opcionais por nome, especieId e tutorId. " +
                             "Use os parametros 'page', 'size' e 'sort' para paginar e ordenar. " +
                             "Ex: /api/pets?nome=rex&page=0&size=5&sort=nome,asc")
    public ResponseEntity<Page<PetResponse>> listar(
            @Parameter(description = "Filtra por trecho do nome") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtra por especie") @RequestParam(required = false) Long especieId,
            @Parameter(description = "Filtra por tutor") @RequestParam(required = false) Long tutorId,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, especieId, tutorId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por id")
    public ResponseEntity<PetResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pet")
    public ResponseEntity<PetResponse> atualizar(@PathVariable Long id,
                                                 @Valid @RequestBody PetRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pet")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

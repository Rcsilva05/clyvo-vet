package br.com.fiap.solin.service;

import br.com.fiap.solin.dto.request.PetRequest;
import br.com.fiap.solin.dto.response.PetResponse;
import br.com.fiap.solin.entity.Especie;
import br.com.fiap.solin.entity.Pet;
import br.com.fiap.solin.entity.Tutor;
import br.com.fiap.solin.exception.RecursoNaoEncontradoException;
import br.com.fiap.solin.mapper.PetMapper;
import br.com.fiap.solin.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;
    private final PetMapper mapper;
    private final TutorService tutorService;
    private final EspecieService especieService;

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse cadastrar(PetRequest dto) {
        Tutor tutor = tutorService.buscarEntidade(dto.tutorId());
        Especie especie = especieService.buscarEntidade(dto.especieId());
        Pet salvo = repository.save(mapper.toEntity(dto, tutor, especie));
        return mapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pets")
    public Page<PetResponse> listar(String nome, Long especieId, Long tutorId, Pageable pageable) {
        return repository.buscarComFiltros(nome, especieId, tutorId, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PetResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<PetResponse> listarPorTutor(Long tutorId, Pageable pageable) {
        return repository.findByTutorId(tutorId, pageable).map(mapper::toResponse);
    }

    /**
     * Mesma listagem por tutor, mas so com pets ativos - usada pela tela
     * MVC "Meus pets" para nao mostrar pets que o tutor ja desativou.
     */
    @Transactional(readOnly = true)
    public Page<PetResponse> listarAtivosPorTutor(Long tutorId, Pageable pageable) {
        return repository.findByTutorIdAndAtivoTrue(tutorId, pageable).map(mapper::toResponse);
    }

    /**
     * Lista pets ativos com os mesmos filtros da API, usada pela tela
     * "Todos os pets" do veterinario.
     */
    @Transactional(readOnly = true)
    public Page<PetResponse> listarAtivos(String nome, Long especieId, Long tutorId, Pageable pageable) {
        return repository.buscarAtivosComFiltros(nome, especieId, tutorId, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse atualizar(Long id, PetRequest dto) {
        Pet pet = buscarEntidade(id);
        Tutor tutor = tutorService.buscarEntidade(dto.tutorId());
        Especie especie = especieService.buscarEntidade(dto.especieId());

        pet.setNome(dto.nome());
        pet.setRaca(dto.raca());
        pet.setDataNascimento(dto.dataNascimento());
        pet.setPesoKg(dto.pesoKg());
        pet.setSexo(dto.sexo());
        pet.setTutor(tutor);
        pet.setEspecie(especie);

        return mapper.toResponse(repository.save(pet));
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Pet", id);
        }
        repository.deleteById(id);
    }

    /**
     * Soft delete: usado pela tela MVC "Meus pets" (botao "Desativar
     * pet"). Diferente de excluir() acima (hard delete, usado pela API
     * REST), aqui o registro do pet e todo o seu historico de eventos e
     * alertas continuam no banco - so ST_ATIVO vira false e o pet some
     * das listagens do dia a dia do tutor e do veterinario.
     */
    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse desativar(Long id) {
        Pet pet = buscarEntidade(id);
        pet.setAtivo(false);
        return mapper.toResponse(repository.save(pet));
    }

    public Pet buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet", id));
    }
}

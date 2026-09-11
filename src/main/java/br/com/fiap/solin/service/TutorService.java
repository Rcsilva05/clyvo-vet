package br.com.fiap.solin.service;

import br.com.fiap.solin.dto.request.TutorRequest;
import br.com.fiap.solin.dto.response.TutorResponse;
import br.com.fiap.solin.entity.Tutor;
import br.com.fiap.solin.exception.RecursoNaoEncontradoException;
import br.com.fiap.solin.exception.RegraNegocioException;
import br.com.fiap.solin.mapper.TutorMapper;
import br.com.fiap.solin.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorRepository repository;
    private final TutorMapper mapper;

    @Transactional
    @CacheEvict(value = "tutores", allEntries = true)
    public TutorResponse cadastrar(TutorRequest dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Ja existe um tutor cadastrado com o email " + dto.email());
        }
        Tutor salvo = repository.save(mapper.toEntity(dto));
        return mapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tutores")
    public Page<TutorResponse> listar(String nome, Pageable pageable) {
        Page<Tutor> page = (nome == null || nome.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNomeContainingIgnoreCase(nome, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tutor", key = "#id")
    public TutorResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    @CacheEvict(value = {"tutores", "tutor"}, allEntries = true)
    public TutorResponse atualizar(Long id, TutorRequest dto) {
        Tutor existente = buscarEntidade(id);
        existente.setNome(dto.nome());
        existente.setEmail(dto.email());
        existente.setTelefone(dto.telefone());
        return mapper.toResponse(repository.save(existente));
    }

    @Transactional
    @CacheEvict(value = {"tutores", "tutor"}, allEntries = true)
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Tutor", id);
        }
        repository.deleteById(id);
    }

    public Tutor buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor", id));
    }
}

package br.com.fiap.solin.service;

import br.com.fiap.solin.dto.request.EspecieRequest;
import br.com.fiap.solin.dto.response.EspecieResponse;
import br.com.fiap.solin.entity.Especie;
import br.com.fiap.solin.exception.RecursoNaoEncontradoException;
import br.com.fiap.solin.mapper.EspecieMapper;
import br.com.fiap.solin.repository.EspecieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EspecieService {

    private final EspecieRepository repository;
    private final EspecieMapper mapper;

    @Transactional
    @CacheEvict(value = "especies", allEntries = true)
    public EspecieResponse cadastrar(EspecieRequest dto) {
        Especie salvo = repository.save(mapper.toEntity(dto));
        return mapper.toResponse(salvo);
    }

    @Cacheable(value = "especies")
    public Page<EspecieResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public EspecieResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    @CacheEvict(value = "especies", allEntries = true)
    public EspecieResponse atualizar(Long id, EspecieRequest dto) {
        Especie e = buscarEntidade(id);
        e.setNome(dto.nome());
        e.setHorasMaximasSemUrinar(dto.horasMaximasSemUrinar());
        return mapper.toResponse(repository.save(e));
    }

    @Transactional
    @CacheEvict(value = "especies", allEntries = true)
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Especie", id);
        }
        repository.deleteById(id);
    }

    public Especie buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especie", id));
    }
}

package br.com.fiap.solin.service;

import br.com.fiap.solin.dto.response.AlertaResponse;
import br.com.fiap.solin.entity.Alerta;
import br.com.fiap.solin.entity.Pet;
import br.com.fiap.solin.exception.RecursoNaoEncontradoException;
import br.com.fiap.solin.mapper.AlertaMapper;
import br.com.fiap.solin.repository.AlertaRepository;
import br.com.fiap.solin.strategy.RegraAlertaStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository repository;
    private final AlertaMapper mapper;

    /**
     * O Spring injeta automaticamente todas as implementacoes de
     * RegraAlertaStrategy em ordem. Isso e a "magica" do Strategy +
     * injecao de dependencias do Spring: novas regras sao plug-and-play.
     */
    private final List<RegraAlertaStrategy> regras;

    /**
     * Executa todas as regras cadastradas para o pet e persiste os
     * alertas que dispararem. Chamado pelo EventoService apos cada
     * registro de evento.
     */
    @Transactional
    public List<Alerta> avaliarRegrasParaPet(Pet pet) {
        return regras.stream()
                .map(r -> r.avaliar(pet))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(repository::save)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponse> listarPorPet(Long petId, Boolean resolvido, Pageable pageable) {
        Page<Alerta> page = (resolvido == null)
                ? repository.findByPetId(petId, pageable)
                : repository.findByPetIdAndResolvido(petId, resolvido, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AlertaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /**
     * Lista alertas para o painel clinico do veterinario, com filtro
     * opcional por status (resolvido/nao resolvido), ordenados do mais
     * recente para o mais antigo.
     */
    @Transactional(readOnly = true)
    public java.util.List<AlertaResponse> listarParaPainel(Boolean resolvido) {
        java.util.List<Alerta> alertas = (resolvido == null)
                ? repository.findAllByOrderByDataHoraGeradoDesc()
                : repository.findByResolvidoOrderByDataHoraGeradoDesc(resolvido);
        return alertas.stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public AlertaResponse marcarComoResolvido(Long id) {
        Alerta alerta = buscarEntidade(id);
        alerta.setResolvido(true);
        return mapper.toResponse(repository.save(alerta));
    }

    private Alerta buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
    }
}

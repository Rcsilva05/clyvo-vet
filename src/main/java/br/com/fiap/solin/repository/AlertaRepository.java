package br.com.fiap.solin.repository;

import br.com.fiap.solin.entity.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    Page<Alerta> findByPetId(Long petId, Pageable pageable);

    Page<Alerta> findByPetIdAndResolvido(Long petId, Boolean resolvido, Pageable pageable);

    List<Alerta> findAllByOrderByDataHoraGeradoDesc();

    List<Alerta> findByResolvidoOrderByDataHoraGeradoDesc(Boolean resolvido);

    long countByResolvido(Boolean resolvido);
}

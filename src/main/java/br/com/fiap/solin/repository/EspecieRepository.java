package br.com.fiap.solin.repository;

import br.com.fiap.solin.entity.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EspecieRepository extends JpaRepository<Especie, Long> {

    Optional<Especie> findByNomeIgnoreCase(String nome);
}

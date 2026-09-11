package br.com.fiap.solin.repository;

import br.com.fiap.solin.entity.Tutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByEmail(String email);

    Page<Tutor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    boolean existsByEmail(String email);
}

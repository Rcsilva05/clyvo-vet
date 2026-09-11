package br.com.fiap.solin.repository;

import br.com.fiap.solin.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Page<Pet> findByTutorId(Long tutorId, Pageable pageable);

    Page<Pet> findByTutorIdAndAtivoTrue(Long tutorId, Pageable pageable);

    Page<Pet> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // CAST(:nome AS string) e necessario aqui: quando o filtro de nome nao e
    // usado (parametro chega null), o driver do PostgreSQL nao consegue
    // inferir o tipo do parametro sozinho e assume "bytea" por padrao, o que
    // quebra o LOWER(...) com "ERRO: nao existe a funcao lower(bytea)".
    // O CAST explicito garante que o Postgres sempre trate o parametro como
    // texto, mesmo quando ele e null.
    @Query("SELECT p FROM Pet p " +
           "WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%'))) " +
           "AND (:especieId IS NULL OR p.especie.id = :especieId) " +
           "AND (:tutorId IS NULL OR p.tutor.id = :tutorId)")
    Page<Pet> buscarComFiltros(@Param("nome") String nome,
                               @Param("especieId") Long especieId,
                               @Param("tutorId") Long tutorId,
                               Pageable pageable);

    // Mesma query de cima, mas restrita a pets ativos - usada pelas telas
    // MVC (tutor e veterinario), que nao devem mostrar pets desativados
    // (soft delete) nas listagens do dia a dia.
    @Query("SELECT p FROM Pet p " +
           "WHERE p.ativo = TRUE " +
           "AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%'))) " +
           "AND (:especieId IS NULL OR p.especie.id = :especieId) " +
           "AND (:tutorId IS NULL OR p.tutor.id = :tutorId)")
    Page<Pet> buscarAtivosComFiltros(@Param("nome") String nome,
                                     @Param("especieId") Long especieId,
                                     @Param("tutorId") Long tutorId,
                                     Pageable pageable);
}

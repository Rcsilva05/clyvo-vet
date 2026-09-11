package br.com.fiap.solin.repository;

import br.com.fiap.solin.entity.Evento;
import br.com.fiap.solin.enums.TipoEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    Page<Evento> findByPetId(Long petId, Pageable pageable);

    /**
     * Retorna o evento mais recente de um determinado tipo para um pet.
     * Usado pelas estrategias de alerta para verificar quanto tempo
     * faz desde a ultima ocorrencia (ex: ultima vez que urinou).
     *
     * Usa Query Method do Spring Data (sem JPQL): findFirstBy... ORDER BY ... DESC.
     * Funciona tanto em Oracle quanto em H2.
     */
    Optional<Evento> findFirstByPetIdAndTipoOrderByDataHoraDesc(Long petId, TipoEvento tipo);

    // ------------------------------------------------------------------
    // IMPORTANTE: por que existem 4 metodos parecidos em vez de 1 so com
    // filtros opcionais (":tipo IS NULL OR e.tipo = :tipo")?
    //
    // No PostgreSQL, quando um parametro de uma query chega como null, o
    // driver nao consegue adivinhar sozinho qual e o tipo de dado daquele
    // parametro (ele nao tem como saber se era pra ser texto, data, enum...)
    // e assume "bytea" (binario) por padrao. Isso quebra a comparacao com
    // um erro tipo "nao foi possivel determinar o tipo de dados do
    // parametro" ou "funcao lower(bytea) nao existe".
    //
    // A forma mais simples e sem risco de dar esse problema de novo e
    // nunca mandar um parametro null para o banco: o service (EventoService)
    // decide, em Java, qual desses metodos chamar de acordo com quais
    // filtros o usuario realmente informou.
    // ------------------------------------------------------------------

    @Query("SELECT e FROM Evento e WHERE e.pet.id = :petId")
    Page<Evento> buscarPorPet(@Param("petId") Long petId, Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.pet.id = :petId AND e.tipo = :tipo")
    Page<Evento> buscarPorPetETipo(@Param("petId") Long petId,
                                   @Param("tipo") TipoEvento tipo,
                                   Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.pet.id = :petId " +
           "AND e.dataHora >= :inicio AND e.dataHora <= :fim")
    Page<Evento> buscarPorPetEPeriodo(@Param("petId") Long petId,
                                      @Param("inicio") LocalDateTime inicio,
                                      @Param("fim") LocalDateTime fim,
                                      Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.pet.id = :petId AND e.tipo = :tipo " +
           "AND e.dataHora >= :inicio AND e.dataHora <= :fim")
    Page<Evento> buscarPorPetTipoEPeriodo(@Param("petId") Long petId,
                                          @Param("tipo") TipoEvento tipo,
                                          @Param("inicio") LocalDateTime inicio,
                                          @Param("fim") LocalDateTime fim,
                                          Pageable pageable);
}

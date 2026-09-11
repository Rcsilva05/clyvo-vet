package br.com.fiap.solin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_ESPECIE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESPECIE")
    private Long id;

    @Column(name = "NM_ESPECIE", nullable = false, unique = true, length = 50)
    private String nome;

    /**
     * Intervalo maximo de horas sem urinar que e considerado normal
     * para esta especie. Usado pelas regras de alerta.
     * Ex: cao adulto ~ 8h, gato ~ 24h.
     */
    @Column(name = "QT_HORAS_MAX_URINA", nullable = false)
    private Integer horasMaximasSemUrinar;

    /**
     * Intervalo maximo de horas sem beber agua que e considerado normal
     * para esta especie. Usado pela segunda regra de alerta (hidratacao),
     * seguindo o mesmo padrao Strategy da regra de urina.
     * Ex: cao adulto ~ 12h, gato ~ 18h.
     */
    @Column(name = "QT_HORAS_MAX_AGUA", nullable = false)
    private Integer horasMaximasSemBeberAgua;
}

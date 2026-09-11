package br.com.fiap.solin.entity;

import br.com.fiap.solin.enums.Sexo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_PET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PET")
    private Long id;

    @Column(name = "NM_PET", nullable = false, length = 80)
    private String nome;

    @Column(name = "DS_RACA", length = 80)
    private String raca;

    @Column(name = "DT_NASCIMENTO")
    private LocalDate dataNascimento;

    @Column(name = "VL_PESO_KG")
    private Double pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_SEXO", length = 10)
    private Sexo sexo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TUTOR", nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESPECIE", nullable = false)
    private Especie especie;

    /**
     * Soft delete: em vez de apagar o pet (e seu historico de eventos e
     * alertas) do banco, "Desativar pet" so marca ST_ATIVO = false. O
     * pet some das listagens normais do tutor e do veterinario, mas o
     * historico clinico continua preservado - importante tanto para
     * auditoria quanto porque um pet pode ter sido cadastrado errado e
     * depois se descobrir que era o mesmo animal (reativacao possivel
     * direto no banco, sem perder nada).
     */
    @Column(name = "ST_ATIVO", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Evento> eventos = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Alerta> alertas = new ArrayList<>();
}

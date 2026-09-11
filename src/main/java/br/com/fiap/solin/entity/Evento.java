package br.com.fiap.solin.entity;

import br.com.fiap.solin.enums.OrigemEvento;
import br.com.fiap.solin.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_EVENTO",
       indexes = {
           @Index(name = "IX_EVENTO_PET_DH", columnList = "ID_PET, DH_EVENTO")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_EVENTO", nullable = false, length = 20)
    private TipoEvento tipo;

    @Column(name = "DH_EVENTO", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_ORIGEM", nullable = false, length = 15)
    private OrigemEvento origem;

    @Column(name = "DS_OBSERVACAO", length = 255)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;
}

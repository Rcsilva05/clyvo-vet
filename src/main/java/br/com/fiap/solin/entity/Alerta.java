package br.com.fiap.solin.entity;

import br.com.fiap.solin.enums.NivelAlerta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ALERTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALERTA")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_NIVEL", nullable = false, length = 10)
    private NivelAlerta nivel;

    @Column(name = "DS_MENSAGEM", nullable = false, length = 255)
    private String mensagem;

    @Column(name = "DH_GERADO", nullable = false)
    private LocalDateTime dataHoraGerado;

    @Column(name = "ST_RESOLVIDO", nullable = false)
    private Boolean resolvido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;
}

package br.com.fiap.solin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_TUTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TUTOR")
    private Long id;

    @Column(name = "NM_TUTOR", nullable = false, length = 120)
    private String nome;

    @Column(name = "DS_EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "NR_TELEFONE", length = 20)
    private String telefone;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDate dataCadastro;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pet> pets = new ArrayList<>();
}

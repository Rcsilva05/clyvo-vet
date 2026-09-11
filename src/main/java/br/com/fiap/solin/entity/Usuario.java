package br.com.fiap.solin.entity;

import br.com.fiap.solin.enums.Perfil;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Usuario de acesso a aplicacao web. Implementa {@link UserDetails} para
 * que o Spring Security consiga autenticar diretamente a partir da
 * entidade JPA, sem precisar de uma classe adaptadora extra.
 *
 * Quando o perfil e TUTOR, o usuario esta vinculado a um registro em
 * {@link Tutor} (relacao 1-para-1) e so deve enxergar os proprios pets.
 * Quando o perfil e VETERINARIO, nao ha vinculo com Tutor: o usuario
 * enxerga o painel clinico completo (todos os pets/alertas).
 */
@Entity
@Table(name = "TB_USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "DS_EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "DS_SENHA_HASH", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "NM_USUARIO", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_PERFIL", nullable = false, length = 20)
    private Perfil perfil;

    @Column(name = "ST_ATIVO", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    // EAGER de proposito: este objeto e carregado uma unica vez no login
    // (dentro da transacao de UsuarioDetailsService) e depois reutilizado,
    // ja "desanexado" do Hibernate, em toda requisicao autenticada durante
    // a sessao HTTP. Um relacionamento LAZY aqui causaria
    // LazyInitializationException ao chamar getTutorId() fora daquela
    // transacao original. O custo extra e minimo (1-para-1, sem colecoes).
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TUTOR")
    private Tutor tutor;

    // ===== Contrato do Spring Security (UserDetails) =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefixo ROLE_ e a convencao esperada por hasRole()/roleHierarchy do Spring Security.
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(ativo);
    }

    /**
     * Id do Tutor vinculado a este usuario, quando o perfil for TUTOR.
     * Usado pelos controllers/services para restringir os dados ao
     * proprio tutor logado.
     */
    public Long getTutorId() {
        return tutor != null ? tutor.getId() : null;
    }
}

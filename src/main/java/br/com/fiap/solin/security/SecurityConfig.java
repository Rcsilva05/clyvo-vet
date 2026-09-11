package br.com.fiap.solin.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Configuracao central do Spring Security.
 *
 * Dois perfis com permissoes diferentes:
 *   - ROLE_TUTOR: area "/tutor/**" (gerencia os proprios pets/eventos)
 *   - ROLE_VETERINARIO: area "/vet/**" (painel clinico Clyvo Vet)
 *
 * As rotas de API REST legadas ("/api/**") tambem exigem autenticacao,
 * evitando expor dados sem controle de acesso; documentacao Swagger
 * fica liberada para fins de avaliacao/demonstracao tecnica.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estaticos e telas publicas
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/login", "/cadastro", "/acesso-negado", "/erro").permitAll()
                // Documentacao tecnica liberada para avaliacao (Swagger)
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                // Area exclusiva do tutor
                .requestMatchers("/tutor/**").hasRole("TUTOR")
                // Area exclusiva do veterinario (Clyvo Vet)
                .requestMatchers("/vet/**").hasRole("VETERINARIO")
                // API REST: qualquer usuario autenticado (os dois perfis) pode consumir
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(perfilSuccessHandler())
                .failureUrl("/login?erro")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acesso-negado")
            )
            // API REST usa Basic Auth (chamadas via Swagger/Postman); telas usam form login
            .httpBasic(basic -> basic.realmName("SOLIN API"))
            // CSRF protege os formularios MVC (token injetado automaticamente pelo
            // thymeleaf-extras-springsecurity6); a API REST e consumida por clientes
            // externos (Postman/Swagger) sem sessao de navegador, entao fica isenta.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    /**
     * Depois do login, redireciona cada perfil para sua propria area,
     * evitando que um tutor caia na tela do veterinario (e vice-versa).
     */
    @Bean
    public AuthenticationSuccessHandler perfilSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isVeterinario = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_VETERINARIO"));
            String destino = isVeterinario ? "/vet/painel" : "/tutor/inicio";
            response.sendRedirect(request.getContextPath() + destino);
        };
    }
}

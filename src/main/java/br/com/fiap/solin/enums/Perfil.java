package br.com.fiap.solin.enums;

/**
 * Perfis de acesso da aplicacao (Spring Security).
 *
 * TUTOR       - gerencia apenas os proprios pets e eventos.
 * VETERINARIO - acessa o painel clinico da Clyvo Vet: ve e resolve
 *               alertas de todos os pets cadastrados no sistema.
 */
public enum Perfil {
    TUTOR,
    VETERINARIO
}

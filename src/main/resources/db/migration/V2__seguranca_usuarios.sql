-- =====================================================================
-- V2: Spring Security - usuarios e perfis de acesso
--
-- Dois perfis (roles) suportados pela aplicacao:
--   TUTOR       -> acessa e gerencia apenas os proprios pets/eventos
--   VETERINARIO -> acessa o painel clinico (Clyvo Vet): ve alertas de
--                  todos os pets e pode resolve-los
--
-- Quando o usuario e do tipo TUTOR, ID_TUTOR aponta para o registro
-- correspondente em TB_TUTOR (1 usuario de login = 1 tutor do dominio).
-- Para VETERINARIO, ID_TUTOR fica nulo.
-- =====================================================================

CREATE TABLE TB_USUARIO (
    ID_USUARIO      BIGSERIAL PRIMARY KEY,
    DS_EMAIL        VARCHAR(150)  NOT NULL UNIQUE,
    DS_SENHA_HASH   VARCHAR(255)  NOT NULL,
    NM_USUARIO      VARCHAR(120)  NOT NULL,
    TP_PERFIL       VARCHAR(20)   NOT NULL,
    ST_ATIVO        BOOLEAN       NOT NULL DEFAULT TRUE,
    ID_TUTOR        BIGINT UNIQUE REFERENCES TB_TUTOR (ID_TUTOR),
    CONSTRAINT CK_USUARIO_PERFIL CHECK (TP_PERFIL IN ('TUTOR', 'VETERINARIO')),
    CONSTRAINT CK_USUARIO_TUTOR_PERFIL CHECK (
        (TP_PERFIL = 'TUTOR' AND ID_TUTOR IS NOT NULL) OR
        (TP_PERFIL = 'VETERINARIO' AND ID_TUTOR IS NULL)
    )
);

COMMENT ON TABLE  TB_USUARIO IS 'Credenciais e perfil de acesso dos usuarios da aplicacao web (Spring Security)';
COMMENT ON COLUMN TB_USUARIO.TP_PERFIL IS 'Perfil de autorizacao: TUTOR ou VETERINARIO';
COMMENT ON COLUMN TB_USUARIO.ID_TUTOR IS 'Vinculo com TB_TUTOR quando o perfil e TUTOR (define quais pets o usuario pode ver)';

CREATE INDEX IX_USUARIO_PERFIL ON TB_USUARIO (TP_PERFIL);

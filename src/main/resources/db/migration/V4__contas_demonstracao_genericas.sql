-- Contas de demonstracao genericas, exibidas na tela de login.
--
-- Nao substituem nem alteram as contas ja existentes (ex: os tutores e o
-- veterinario cadastrados na V3) - sao contas NOVAS, criadas so para que
-- a tela de login possa mostrar um exemplo de acesso sem expor nomes ou
-- emails de pessoas reais.
--
-- Senha em texto puro para ambas: demo123

INSERT INTO TB_TUTOR (NM_TUTOR, DS_EMAIL, NR_TELEFONE, DT_CADASTRO) VALUES
    ('Tutor Demonstração', 'tutor.demo@solin.com', '(11) 90000-0001', CURRENT_DATE);

INSERT INTO TB_USUARIO (DS_EMAIL, DS_SENHA_HASH, NM_USUARIO, TP_PERFIL, ST_ATIVO, ID_TUTOR) VALUES
    ('tutor.demo@solin.com', '$2b$10$mhv.Z1tvkfVy6zHzqwdWte/xH2oi57ZEx8xxzMyCz9JP5g7tAu6pW', 'Tutor Demonstração', 'TUTOR', TRUE,
     (SELECT ID_TUTOR FROM TB_TUTOR WHERE DS_EMAIL = 'tutor.demo@solin.com'));

INSERT INTO TB_USUARIO (DS_EMAIL, DS_SENHA_HASH, NM_USUARIO, TP_PERFIL, ST_ATIVO, ID_TUTOR) VALUES
    ('vet.demo@clyvovet.com.br', '$2b$10$mhv.Z1tvkfVy6zHzqwdWte/xH2oi57ZEx8xxzMyCz9JP5g7tAu6pW', 'Veterinário Demonstração', 'VETERINARIO', TRUE, NULL);

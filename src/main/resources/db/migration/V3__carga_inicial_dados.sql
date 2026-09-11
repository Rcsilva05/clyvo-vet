-- =====================================================================
-- V3: Carga inicial de dados (seed)
--
-- Popula especies basicas, um veterinario, dois tutores com seus pets
-- e um historico de eventos que ja demonstra o motor de alertas
-- funcionando (um pet em situacao normal, outro gerando alerta).
--
-- Senhas de teste (ja com hash BCrypt, texto puro so aqui no comentario):
--   marcel.wagner@clyvovet.com.br / tranquilo123
--   maria.silva@email.com       / tutor123
--   joao.pereira@email.com      / tutor123
-- =====================================================================

INSERT INTO TB_ESPECIE (NM_ESPECIE, QT_HORAS_MAX_URINA) VALUES
    ('Cao',    8),
    ('Gato',   24),
    ('Coelho', 12);

-- ----- Tutores -----
INSERT INTO TB_TUTOR (NM_TUTOR, DS_EMAIL, NR_TELEFONE, DT_CADASTRO) VALUES
    ('Maria Silva',   'maria.silva@email.com',   '(11) 91234-5678', CURRENT_DATE - INTERVAL '90 days'),
    ('Joao Pereira',  'joao.pereira@email.com',  '(11) 99876-5432', CURRENT_DATE - INTERVAL '45 days');

-- ----- Usuarios de acesso (Spring Security) -----
-- Perfil VETERINARIO: enxerga o painel clinico da Clyvo Vet por completo
INSERT INTO TB_USUARIO (DS_EMAIL, DS_SENHA_HASH, NM_USUARIO, TP_PERFIL, ST_ATIVO, ID_TUTOR) VALUES
    ('marcel.wagner@clyvovet.com.br', '$2b$10$UDGrNYHN9JoaxBMgNcstA.waZoX3RVNquj066KNBhK696UfDg1ta2', 'Dr. Marcel Stefan Wagner', 'VETERINARIO', TRUE, NULL);

-- Perfil TUTOR: cada usuario TUTOR fica amarrado ao registro correspondente em TB_TUTOR
INSERT INTO TB_USUARIO (DS_EMAIL, DS_SENHA_HASH, NM_USUARIO, TP_PERFIL, ST_ATIVO, ID_TUTOR) VALUES
    ('maria.silva@email.com',  '$2b$10$KFevJXEdVdmdbDwnd/VRhe1M48.uO6NuUKd/R7MxFlu.hbO79T8Yi', 'Maria Silva',  'TUTOR', TRUE, (SELECT ID_TUTOR FROM TB_TUTOR WHERE DS_EMAIL = 'maria.silva@email.com')),
    ('joao.pereira@email.com', '$2b$10$KFevJXEdVdmdbDwnd/VRhe1M48.uO6NuUKd/R7MxFlu.hbO79T8Yi', 'Joao Pereira', 'TUTOR', TRUE, (SELECT ID_TUTOR FROM TB_TUTOR WHERE DS_EMAIL = 'joao.pereira@email.com'));

-- ----- Pets -----
INSERT INTO TB_PET (NM_PET, DS_RACA, DT_NASCIMENTO, VL_PESO_KG, TP_SEXO, ID_TUTOR, ID_ESPECIE) VALUES
    ('Rex',    'Labrador',           '2021-03-10', 28.5, 'MACHO', (SELECT ID_TUTOR FROM TB_TUTOR WHERE DS_EMAIL = 'maria.silva@email.com'), (SELECT ID_ESPECIE FROM TB_ESPECIE WHERE NM_ESPECIE = 'Cao')),
    ('Mimi',   'Siames',             '2020-07-22',  4.2, 'FEMEA', (SELECT ID_TUTOR FROM TB_TUTOR WHERE DS_EMAIL = 'maria.silva@email.com'), (SELECT ID_ESPECIE FROM TB_ESPECIE WHERE NM_ESPECIE = 'Gato')),
    ('Thor',   'Vira-lata caramelo', '2019-11-05', 18.0, 'MACHO', (SELECT ID_TUTOR FROM TB_TUTOR WHERE DS_EMAIL = 'joao.pereira@email.com'), (SELECT ID_ESPECIE FROM TB_ESPECIE WHERE NM_ESPECIE = 'Cao'));

-- ----- Eventos -----
-- Rex: rotina normal, ultimo evento de urina ha poucas horas (sem alerta)
INSERT INTO TB_EVENTO (TP_EVENTO, DH_EVENTO, TP_ORIGEM, DS_OBSERVACAO, ID_PET) VALUES
    ('URINOU',      CURRENT_TIMESTAMP - INTERVAL '2 hours',  'SENSOR_IOT', 'Registrado pelo sensor da caixa de areia',  (SELECT ID_PET FROM TB_PET WHERE NM_PET = 'Rex')),
    ('COMEU',       CURRENT_TIMESTAMP - INTERVAL '5 hours',  'MANUAL',     'Racao pela manha',                          (SELECT ID_PET FROM TB_PET WHERE NM_PET = 'Rex')),
    ('BEBEU_AGUA',  CURRENT_TIMESTAMP - INTERVAL '3 hours',  'MANUAL',     NULL,                                         (SELECT ID_PET FROM TB_PET WHERE NM_PET = 'Rex'));

-- Mimi: ultima urina ha muito tempo -> deve gerar alerta VERMELHO na primeira avaliacao (limite gato = 24h, critico = 48h)
INSERT INTO TB_EVENTO (TP_EVENTO, DH_EVENTO, TP_ORIGEM, DS_OBSERVACAO, ID_PET) VALUES
    ('URINOU',      CURRENT_TIMESTAMP - INTERVAL '52 hours', 'SENSOR_IOT', 'Ultimo registro do sensor',                  (SELECT ID_PET FROM TB_PET WHERE NM_PET = 'Mimi')),
    ('COMEU',       CURRENT_TIMESTAMP - INTERVAL '10 hours', 'MANUAL',     'Comeu pouco',                                (SELECT ID_PET FROM TB_PET WHERE NM_PET = 'Mimi'));

-- Thor: rotina normal
INSERT INTO TB_EVENTO (TP_EVENTO, DH_EVENTO, TP_ORIGEM, DS_OBSERVACAO, ID_PET) VALUES
    ('URINOU',      CURRENT_TIMESTAMP - INTERVAL '1 hours',  'MANUAL',     NULL, (SELECT ID_PET FROM TB_PET WHERE NM_PET = 'Thor'));

-- ----- Alerta ja existente para Mimi (demonstra o painel do veterinario com dado real desde o primeiro acesso) -----
INSERT INTO TB_ALERTA (TP_NIVEL, DS_MENSAGEM, DH_GERADO, ST_RESOLVIDO, ID_PET) VALUES
    ('VERMELHO', 'URGENTE: o pet Mimi esta ha 52 horas sem urinar. Contate o veterinario.', CURRENT_TIMESTAMP - INTERVAL '1 hours', FALSE, (SELECT ID_PET FROM TB_PET WHERE NM_PET = 'Mimi'));

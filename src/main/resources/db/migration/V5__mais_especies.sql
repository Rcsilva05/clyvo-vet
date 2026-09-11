-- Renomeia "Cao" para "Cachorro" (mesmo registro, mesmo ID_ESPECIE - os
-- pets ja cadastrados com essa especie continuam vinculados normalmente,
-- so muda o nome exibido na tela).
UPDATE TB_ESPECIE SET NM_ESPECIE = 'Cachorro' WHERE NM_ESPECIE = 'Cao';

-- Novas especies de pets, cada uma ja com seu limite de horas sem urinar
-- configurado, para que o motor de alertas (Strategy) funcione normalmente
-- tambem para elas assim que forem selecionadas no cadastro de um pet.
INSERT INTO TB_ESPECIE (NM_ESPECIE, QT_HORAS_MAX_URINA) VALUES
    ('Hamster',             6),
    ('Papagaio',           10),
    ('Peixe',              24),
    ('Tartaruga',          24),
    ('Porquinho-da-índia',  6),
    ('Calopsita',          10);

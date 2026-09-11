-- Segunda regra de alerta (Strategy): hidratacao. Alem de "sem urinar",
-- o SOLIN passa a acompanhar tambem "sem beber agua", reaproveitando o
-- mesmo padrao Strategy ja usado pela regra de urina (RegraAlertaStrategy).
--
-- Adiciona o novo limite em duas etapas porque a coluna e NOT NULL e a
-- tabela TB_ESPECIE ja tem linhas (inseridas nas V3 e V5): primeiro cria
-- a coluna aceitando nulo, popula um valor por especie, so depois trava
-- o NOT NULL - assim nenhuma linha existente fica inconsistente.
ALTER TABLE TB_ESPECIE ADD COLUMN QT_HORAS_MAX_AGUA INTEGER;

UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 12 WHERE NM_ESPECIE = 'Cachorro';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 18 WHERE NM_ESPECIE = 'Gato';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 10 WHERE NM_ESPECIE = 'Coelho';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 8  WHERE NM_ESPECIE = 'Hamster';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 14 WHERE NM_ESPECIE = 'Papagaio';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 30 WHERE NM_ESPECIE = 'Peixe';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 30 WHERE NM_ESPECIE = 'Tartaruga';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 8  WHERE NM_ESPECIE = 'Porquinho-da-índia';
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 14 WHERE NM_ESPECIE = 'Calopsita';

-- Rede de seguranca: qualquer especie que por algum motivo tenha ficado
-- sem valor (ex: cadastrada manualmente entre migrations) recebe um
-- padrao conservador de 12h antes do NOT NULL travar.
UPDATE TB_ESPECIE SET QT_HORAS_MAX_AGUA = 12 WHERE QT_HORAS_MAX_AGUA IS NULL;

ALTER TABLE TB_ESPECIE ALTER COLUMN QT_HORAS_MAX_AGUA SET NOT NULL;

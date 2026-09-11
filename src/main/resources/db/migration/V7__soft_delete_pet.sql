-- Soft delete para pets: em vez de apagar um pet cadastrado errado (e
-- perder o historico de eventos/alertas dele), o tutor pode "desativar"
-- o pet, que so marca ST_ATIVO = false. As listagens do tutor e do
-- veterinario passam a considerar apenas pets ativos.
--
-- NOT NULL DEFAULT TRUE garante que os pets ja cadastrados (V3) continuam
-- todos visiveis normalmente apos esta migration.
ALTER TABLE TB_PET ADD COLUMN ST_ATIVO BOOLEAN NOT NULL DEFAULT TRUE;

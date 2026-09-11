-- =====================================================================
-- V1: Schema inicial do dominio SOLIN
-- Monitoramento de saude de pets (Tutor, Pet, Especie, Evento, Alerta)
-- =====================================================================

CREATE TABLE TB_ESPECIE (
    ID_ESPECIE            BIGSERIAL PRIMARY KEY,
    NM_ESPECIE             VARCHAR(50)  NOT NULL UNIQUE,
    QT_HORAS_MAX_URINA     INTEGER      NOT NULL
);

COMMENT ON TABLE  TB_ESPECIE IS 'Especies de pets suportadas (cao, gato, coelho, etc.)';
COMMENT ON COLUMN TB_ESPECIE.QT_HORAS_MAX_URINA IS 'Intervalo maximo de horas sem urinar considerado normal para a especie';

CREATE TABLE TB_TUTOR (
    ID_TUTOR        BIGSERIAL PRIMARY KEY,
    NM_TUTOR        VARCHAR(120)  NOT NULL,
    DS_EMAIL        VARCHAR(150)  NOT NULL UNIQUE,
    NR_TELEFONE     VARCHAR(20),
    DT_CADASTRO     DATE          NOT NULL
);

COMMENT ON TABLE TB_TUTOR IS 'Tutores (donos) dos pets monitorados';

CREATE TABLE TB_PET (
    ID_PET             BIGSERIAL PRIMARY KEY,
    NM_PET              VARCHAR(80)   NOT NULL,
    DS_RACA             VARCHAR(80),
    DT_NASCIMENTO       DATE,
    VL_PESO_KG          DOUBLE PRECISION,
    TP_SEXO             VARCHAR(10),
    ID_TUTOR            BIGINT NOT NULL REFERENCES TB_TUTOR (ID_TUTOR),
    ID_ESPECIE          BIGINT NOT NULL REFERENCES TB_ESPECIE (ID_ESPECIE)
);

COMMENT ON TABLE TB_PET IS 'Pets monitorados pelo sistema, vinculados a um tutor e uma especie';

CREATE INDEX IX_PET_TUTOR   ON TB_PET (ID_TUTOR);
CREATE INDEX IX_PET_ESPECIE ON TB_PET (ID_ESPECIE);

CREATE TABLE TB_EVENTO (
    ID_EVENTO       BIGSERIAL PRIMARY KEY,
    TP_EVENTO       VARCHAR(20)   NOT NULL,
    DH_EVENTO       TIMESTAMP     NOT NULL,
    TP_ORIGEM       VARCHAR(15)   NOT NULL,
    DS_OBSERVACAO   VARCHAR(255),
    ID_PET          BIGINT NOT NULL REFERENCES TB_PET (ID_PET)
);

COMMENT ON TABLE TB_EVENTO IS 'Eventos de saude/rotina registrados para um pet (urinou, comeu, bebeu agua, etc.)';

CREATE INDEX IX_EVENTO_PET_DH ON TB_EVENTO (ID_PET, DH_EVENTO);

CREATE TABLE TB_ALERTA (
    ID_ALERTA           BIGSERIAL PRIMARY KEY,
    TP_NIVEL             VARCHAR(10)   NOT NULL,
    DS_MENSAGEM          VARCHAR(255)  NOT NULL,
    DH_GERADO            TIMESTAMP     NOT NULL,
    ST_RESOLVIDO         BOOLEAN       NOT NULL DEFAULT FALSE,
    ID_PET               BIGINT NOT NULL REFERENCES TB_PET (ID_PET)
);

COMMENT ON TABLE TB_ALERTA IS 'Alertas gerados automaticamente pelas regras de negocio (Strategy) quando um pet foge do padrao esperado';

CREATE INDEX IX_ALERTA_PET       ON TB_ALERTA (ID_PET);
CREATE INDEX IX_ALERTA_RESOLVIDO ON TB_ALERTA (ST_RESOLVIDO);

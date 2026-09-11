# Diagrama Entidade–Relacionamento (DER)

> Modelo físico do banco de dados Oracle/H2 do projeto SOLIN.

```mermaid
erDiagram
    TB_TUTOR ||--o{ TB_PET : "possui"
    TB_ESPECIE ||--o{ TB_PET : "classifica"
    TB_PET ||--o{ TB_EVENTO : "registra"
    TB_PET ||--o{ TB_ALERTA : "recebe"

    TB_TUTOR {
        NUMBER id_tutor PK
        VARCHAR2 nm_tutor "NOT NULL"
        VARCHAR2 ds_email "NOT NULL UNIQUE"
        VARCHAR2 nr_telefone
        DATE dt_cadastro "NOT NULL"
    }

    TB_ESPECIE {
        NUMBER id_especie PK
        VARCHAR2 nm_especie "NOT NULL UNIQUE"
        NUMBER qt_horas_max_urina "NOT NULL"
    }

    TB_PET {
        NUMBER id_pet PK
        VARCHAR2 nm_pet "NOT NULL"
        VARCHAR2 ds_raca
        DATE dt_nascimento
        NUMBER vl_peso_kg
        VARCHAR2 tp_sexo
        NUMBER id_tutor FK "NOT NULL"
        NUMBER id_especie FK "NOT NULL"
    }

    TB_EVENTO {
        NUMBER id_evento PK
        VARCHAR2 tp_evento "NOT NULL"
        TIMESTAMP dh_evento "NOT NULL"
        VARCHAR2 tp_origem "NOT NULL"
        VARCHAR2 ds_observacao
        NUMBER id_pet FK "NOT NULL"
    }

    TB_ALERTA {
        NUMBER id_alerta PK
        VARCHAR2 tp_nivel "NOT NULL"
        VARCHAR2 ds_mensagem "NOT NULL"
        TIMESTAMP dh_gerado "NOT NULL"
        NUMBER st_resolvido "NOT NULL"
        NUMBER id_pet FK "NOT NULL"
    }
```

## Coerência com o Diagrama de Classes

Cada entidade JPA do diagrama de classes mapeia diretamente para uma tabela aqui:

| Classe (JPA) | Tabela | PK |
|---|---|---|
| `Tutor` | `TB_TUTOR` | `id_tutor` |
| `Especie` | `TB_ESPECIE` | `id_especie` |
| `Pet` | `TB_PET` | `id_pet` |
| `Evento` | `TB_EVENTO` | `id_evento` |
| `Alerta` | `TB_ALERTA` | `id_alerta` |

Os enums (`TipoEvento`, `NivelAlerta`, `OrigemEvento`, `Sexo`) são persistidos como `VARCHAR2` pelo Hibernate via `@Enumerated(EnumType.STRING)` — opção escolhida ao invés de `ORDINAL` porque manter a string facilita a leitura direta no banco e evita problemas se a ordem dos valores do enum mudar no futuro.

## Índices

| Índice | Tabela | Colunas | Motivo |
|---|---|---|---|
| `IX_EVENTO_PET_DH` | `TB_EVENTO` | `(id_pet, dh_evento)` | Acelera a query `findUltimoEventoPorTipo` usada pelas Strategies de alerta |

## Cascateamento

Quando um `Tutor` é excluído, todos os `Pets` dele são excluídos (cascade). Quando um `Pet` é excluído, todos os `Eventos` e `Alertas` dele também são (cascade + orphanRemoval). Isso evita registros órfãos no banco.

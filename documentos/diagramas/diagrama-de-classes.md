# Diagrama de Classes — Entidades SOLIN

> Representação das entidades do domínio, atributos principais e relacionamentos JPA.

```mermaid
classDiagram
    class Tutor {
        +Long id
        +String nome
        +String email
        +String telefone
        +LocalDate dataCadastro
        +List~Pet~ pets
    }

    class Pet {
        +Long id
        +String nome
        +String raca
        +LocalDate dataNascimento
        +Double pesoKg
        +Sexo sexo
        +Tutor tutor
        +Especie especie
        +List~Evento~ eventos
        +List~Alerta~ alertas
    }

    class Especie {
        +Long id
        +String nome
        +Integer horasMaximasSemUrinar
    }

    class Evento {
        +Long id
        +TipoEvento tipo
        +LocalDateTime dataHora
        +OrigemEvento origem
        +String observacao
        +Pet pet
    }

    class Alerta {
        +Long id
        +NivelAlerta nivel
        +String mensagem
        +LocalDateTime dataHoraGerado
        +Boolean resolvido
        +Pet pet
    }

    class TipoEvento {
        <<enumeration>>
        URINOU
        NAO_URINOU
        DEFECOU
        COMEU
        BEBEU_AGUA
        MEDICAMENTO
    }

    class NivelAlerta {
        <<enumeration>>
        AMARELO
        VERMELHO
    }

    class OrigemEvento {
        <<enumeration>>
        MANUAL
        SENSOR_IOT
    }

    class Sexo {
        <<enumeration>>
        MACHO
        FEMEA
    }

    Tutor "1" --o "0..*" Pet : possui
    Especie "1" --o "0..*" Pet : classifica
    Pet "1" --o "0..*" Evento : registra
    Pet "1" --o "0..*" Alerta : recebe
    Evento --> TipoEvento
    Evento --> OrigemEvento
    Alerta --> NivelAlerta
    Pet --> Sexo
```

## Relacionamentos e constraints

| Relação | Cardinalidade | Constraint |
|---|---|---|
| Tutor → Pet | 1 : N | Pet **NOT NULL** em `id_tutor`. Cascade ALL + orphanRemoval no Tutor |
| Especie → Pet | 1 : N | Pet **NOT NULL** em `id_especie` |
| Pet → Evento | 1 : N | Evento **NOT NULL** em `id_pet`. Cascade ALL + orphanRemoval |
| Pet → Alerta | 1 : N | Alerta **NOT NULL** em `id_pet`. Cascade ALL + orphanRemoval |

### Outras constraints
- `Tutor.email` é **UNIQUE** e NOT NULL
- `Especie.nome` é **UNIQUE**
- `Evento` tem índice composto em `(id_pet, dh_evento)` para acelerar a query do último evento por tipo (usada pelo Strategy de alerta)
- Todos os relacionamentos `@ManyToOne` usam `FetchType.LAZY` para evitar joins desnecessários

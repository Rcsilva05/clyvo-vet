package br.com.fiap.solin.strategy;

import br.com.fiap.solin.entity.Alerta;
import br.com.fiap.solin.entity.Pet;

import java.util.Optional;

/**
 * Design Pattern: Strategy.
 *
 * Cada regra de alerta e uma implementacao desta interface.
 * O AlertaService usa todas as implementacoes disponiveis (injetadas
 * automaticamente como List pelo Spring) e dispara as que se aplicam.
 *
 * Vantagem: adicionar uma nova regra (ex: "pet nao bebeu agua em 8 horas")
 * e so criar uma nova classe implementando esta interface. Nenhum codigo
 * existente precisa ser alterado (Open/Closed Principle).
 */
public interface RegraAlertaStrategy {

    /**
     * Avalia se a regra dispara um alerta para o pet informado.
     * Retorna o Alerta caso dispare, ou Optional.empty() caso contrario.
     */
    Optional<Alerta> avaliar(Pet pet);
}

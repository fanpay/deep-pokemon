/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.TurnPlayerStat;
import com.mimosa.deeppokemon.entity.stat.TurnPokemonStat;
import com.mimosa.deeppokemon.entity.stat.TurnStat;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class WinEventAnalyzer implements BattleEventAnalyzer {
    private static final String WIN = "win";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(WIN);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        setLastTurnStat(battleStat, battleContext);
    }

    private void setLastTurnStat(BattleStat battleStat, BattleContext battleContext) {
        TurnStat turnStat = new TurnStat(battleContext.getTurn());
        for (PlayerStatus playerStatus : battleContext.getPlayerStatusList()) {
            TurnPlayerStat turnPlayerStat = new TurnPlayerStat();
            BigDecimal totalHealth = BigDecimal.ZERO;
            for (PokemonStatus pokemonStatus : playerStatus.getPokemonStatusMap().values()) {
                TurnPokemonStat turnPokemonStat = new TurnPokemonStat(pokemonStatus.getPokemonName());
                turnPokemonStat.setHealth(pokemonStatus.getHealth());
                turnPlayerStat.addTurnPokemonStat(turnPokemonStat);
                totalHealth = totalHealth.add(pokemonStatus.getHealth());
            }
            turnPlayerStat.setTotalHealth(totalHealth);
            turnStat.getTurnPlayerStatList().add(turnPlayerStat);
        }
        battleStat.turnStats().add(turnStat);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}
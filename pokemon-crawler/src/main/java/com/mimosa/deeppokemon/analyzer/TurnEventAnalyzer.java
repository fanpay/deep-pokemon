/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.TurnPlayerStat;
import com.mimosa.deeppokemon.entity.stat.TurnPokemonStat;
import com.mimosa.deeppokemon.entity.stat.TurnStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class TurnEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(TurnEventAnalyzer.class);
    private static final String TURN = "turn";
    private static final String START = "start";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(START, TURN);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (START.equals(battleEvent.getType())) {
            return;
        }

        if (battleEvent.getContents().isEmpty()) {
            logger.warn("can not match battle turn by content {}", battleEvent);
        }
        battleContext.setTurn(Integer.parseInt(battleEvent.getContents().get(0)));
        battleContext.getPlayerStatusList().forEach(playerStatus -> {
            playerStatus.setTurnStartPokemonName(battleContext.getTurn(), playerStatus.getActivePokemonName());
            playerStatus.getPokemonStatus(playerStatus.getActivePokemonName())
                    .setLastActivateTurn(battleContext.getTurn());
        });

        setLastTurnStat(battleStat, battleContext);
    }

    private void setLastTurnStat(BattleStat battleStat, BattleContext battleContext) {
        TurnStat turnStat = new TurnStat(battleContext.getTurn() - 1);
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
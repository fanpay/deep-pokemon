/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;

public interface BattleEventAnalyzer {
    /**
     * analyze event and fill battle stats and  battle status
     *
     * @param battleEvent
     * @param battleStat all player battle stat
     * @param battleStatus  all player battle status
     */
    void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus);

    boolean supportAnalyze(BattleEvent battleEvent);
}
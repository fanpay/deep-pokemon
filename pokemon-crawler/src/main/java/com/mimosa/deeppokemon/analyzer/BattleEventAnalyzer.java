/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.entity.stat.BattleStat;

public interface BattleEventAnalyzer {
    /**
     * analyze event and fill battle stats and  battle status
     *
     * @param battleEvent
     * @param battleStat all player battle stat
     * @param battleContext  all player battle status
     */
    void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext);

    boolean supportAnalyze(BattleEvent battleEvent);
}
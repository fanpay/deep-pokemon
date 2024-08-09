/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MoveEventAnalyzerTest {
    @Autowired
    private MoveEventAnalyzer moveEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("move", List.of("p1a: YOUCANTBREAKME", "Protect", "p1a: Gliscor"),
                null, null);
        PlayerStat p1 = new PlayerStat(1, "");
        PokemonBattleStat gliscor = new PokemonBattleStat("Gliscor");
        p1.addPokemonBattleStat(gliscor);
        BattleStat battleStat = new BattleStat(null, List.of(p1), new ArrayList<>());

        PlayerStatus p1Stauts = new PlayerStatus();
        p1Stauts.setPokemonNickName("YOUCANTBREAKME", "Gliscor");
        BattleContext battleContext = new BattleContext(List.of(p1Stauts));
        Assertions.assertTrue(moveEventAnalyzer.supportAnalyze(battleEvent));
        moveEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Assertions.assertEquals(1, gliscor.getMoveCount());
        Assertions.assertEquals(1, p1.getMoveCount());
        assertInstanceOf(MoveEventStat.class, battleEvent.getBattleEventStat());
        MoveEventStat stat = (MoveEventStat) battleEvent.getBattleEventStat();
        Assertions.assertEquals(1, stat.eventTarget().playerNumber());
        Assertions.assertEquals("Gliscor", stat.eventTarget().targetName());
        Assertions.assertEquals("YOUCANTBREAKME", stat.eventTarget().nickName());
        Assertions.assertEquals("Protect", stat.moveName());
    }
}
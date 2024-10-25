/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleHighLight;import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SideEndEventAnalyzerTest {

    public static final String CELEBI = "Celebi";
    @Autowired
    private SideEndEventAnalyzer sideEndEventAnalyzer;

    @Test
    void analyze() {
        String stealthRock = "Stealth Rock";
        BattleEvent sideEndEvent = new BattleEvent("sideend", List.of("p2: SOULWIND", stealthRock, "[from] move: " +
                "Rapid Spin", "[of] p2a: Starmie"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .setTurn(4)
                .addSide(2, new Side(stealthRock, new EventTarget(1, CELEBI, CELEBI)))
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .build();
        Assertions.assertTrue(sideEndEventAnalyzer.supportAnalyze(sideEndEvent));
        sideEndEventAnalyzer.analyze(sideEndEvent, battleStat, battleContext);
        Assertions.assertEquals(0, battleContext.getPlayerStatusList().get(1).getSideList().size());
        Assertions.assertEquals(1, battleStat.playerStatList().get(1).getHighLights().size());
        BattleHighLight highLight = battleStat.playerStatList().get(1).getHighLights().get(0);
        Assertions.assertEquals(4, highLight.turn());
        Assertions.assertEquals(BattleHighLight.HighLightType.END_SIDE, highLight.type());
        Assertions.assertNotNull(highLight.description());
    }
}
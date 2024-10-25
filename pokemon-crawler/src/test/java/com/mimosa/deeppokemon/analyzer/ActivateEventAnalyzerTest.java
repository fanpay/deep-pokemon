/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.ActivateStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivateEventAnalyzerTest {
    private static final String IRON_MOTH = "Iron Moth";
    private static final String TOXAPEX = "Toxapex";
    protected static final String URSHIFU_RAPID_STRIKE = "Urshifu-Rapid-Strike";
    @Autowired
    private ActivateEventAnalyzer activateEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("activate", List.of("p1a: oops??", "move: Infestation",
                "[of] p2a: Toxapex"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, IRON_MOTH, "oops??")
                .addPokemon(2, TOXAPEX, TOXAPEX)
                .build();

        BattleStat battleStat = new BattleStatBuilder().build();
        assertTrue(activateEventAnalyzer.supportAnalyze(battleEvent));
        activateEventAnalyzer.analyze(battleEvent, battleStat, battleContext);

        PokemonStatus pokemonStatus = battleContext.getPlayerStatusList().get(0).getPokemonStatus(IRON_MOTH);
        assertEquals(1, pokemonStatus.getActivateStatusList().size());
        ActivateStatus activateStatus = pokemonStatus.getActivateStatusList().get(0);
        assertEquals("move: Infestation", activateStatus.content());
        assertEquals("move", activateStatus.type());
        assertEquals("Infestation", activateStatus.status());
        assertNotNull(activateStatus.ofTarget());
        assertEquals(2, activateStatus.ofTarget().playerNumber());
        assertEquals(TOXAPEX, activateStatus.ofTarget().targetName());
    }

    @Test
    void analyzeItem() {
        BattleEvent battleEvent = new BattleEvent("activate", List.of("p1a: Urshifu", "item: Protective Pads"), null,
                null);
        Battle battle = new BattleBuilder()
                .addPokemon(1, "Urshifu-*")
                .build();

        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, URSHIFU_RAPID_STRIKE, "Urshifu")
                .setBattle(battle)
                .build();


        BattleStat battleStat = new BattleStatBuilder().build();
        assertTrue(activateEventAnalyzer.supportAnalyze(battleEvent));
        activateEventAnalyzer.analyze(battleEvent, battleStat, battleContext);

        Pokemon pokemon = battleContext.getBattle().getBattleTeams().get(0).findPokemon("Urshifu-*");
        assertEquals("Protective Pads", pokemon.getItem());
    }
}
/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Field;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class HealEventAnalyzerTest {
    private static final String RILLABOOM = "Rillaboom";
    private static final String CORVIKNIGHT = "Corviknight";
    private static final String TING_LU = "Ting-Lu";
    private static final String GLISCOR = "Gliscor";
    protected static final String DARKRAI = "Darkrai";
    protected static final String HATTERENE = "Hatterene";
    protected static final String ALOMOMOLA = "Alomomola";
    @Autowired
    private HealEventAnalyzer healEventAnalyzer;

    @Test
    void analyzeMoveHeal() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(1, CORVIKNIGHT, "in a groove"), "Roost"));
        BattleEvent healthEvent = new BattleEvent("heal", List.of("p1a: in a groove", "99/100"), moveEvent, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, RILLABOOM)
                .addPokemonStat(1, CORVIKNIGHT)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, RILLABOOM, RILLABOOM)
                .addPokemon(1, CORVIKNIGHT, "in a groove")
                .setTurnStartPokemon(2, RILLABOOM)
                .setHealth(1, CORVIKNIGHT, BigDecimal.valueOf(49))
                .build();

        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleContext);

        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(CORVIKNIGHT);
        PokemonBattleStat rillaboomStat = battleStat.playerStatList().get(1).getPokemonBattleStat(RILLABOOM);
        assertEquals(BigDecimal.valueOf(50.0), corviknightStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), corviknightStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(-50.0), rillaboomStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-50.0), rillaboomStat.getAttackValue());

        PokemonStatus corviknightStatus = battleContext.getPlayerStatusList().get(0).getPokemonStatus(CORVIKNIGHT);
        assertEquals(BigDecimal.valueOf(99.0), corviknightStatus.getHealth());
    }

    @Test
    void analyzeItemHeal() {
        BattleEvent healthEvent = new BattleEvent("heal",
                List.of("p2a: Ting-Lu", "95/100", "[from] item: Leftovers"), null, null, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, TING_LU)
                .addPokemonStat(1, CORVIKNIGHT)
                .build();
        Battle battle = new BattleBuilder()
                .addPokemon(2, TING_LU)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, TING_LU, TING_LU)
                .addPokemon(1, CORVIKNIGHT, "in a groove")
                .setTurnStartPokemon(1, CORVIKNIGHT)
                .setHealth(2, TING_LU, BigDecimal.valueOf(89))
                .setBattle(battle)
                .build();
        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleContext);

        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(CORVIKNIGHT);
        PokemonBattleStat tiluStat = battleStat.playerStatList().get(1).getPokemonBattleStat(TING_LU);
        assertEquals(BigDecimal.valueOf(-6.0), corviknightStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-6.0), corviknightStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(6.0), tiluStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), tiluStat.getAttackValue());

        PokemonStatus tiluStatus = battleContext.getPlayerStatusList().get(1).getPokemonStatus(TING_LU);
        assertEquals(BigDecimal.valueOf(95.0), tiluStatus.getHealth());
        assertEquals("Leftovers", battle.getBattleTeams().get(1).findPokemon(TING_LU).getItem());
    }

    @Test
    void analyzeAbilityHeal() {
        BattleEvent healthEvent = new BattleEvent("heal",
                List.of("p1a: Gliscor", "97/100 tox", "[from] ability: Poison Heal"), null, null, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, TING_LU)
                .addPokemonStat(1, GLISCOR)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, TING_LU, TING_LU)
                .addPokemon(1, GLISCOR, GLISCOR)
                .setTurnStartPokemon(2, TING_LU)
                .setHealth(1, GLISCOR, BigDecimal.valueOf(84))
                .build();
        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleContext);

        PokemonBattleStat gliscorStat = battleStat.playerStatList().get(0).getPokemonBattleStat(GLISCOR);
        PokemonBattleStat tingluStat = battleStat.playerStatList().get(1).getPokemonBattleStat(TING_LU);
        assertEquals(BigDecimal.valueOf(13.0), gliscorStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), gliscorStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(-13.0), tingluStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-13.0), tingluStat.getAttackValue());

        PokemonStatus gliscorStatus = battleContext.getPlayerStatusList().get(0).getPokemonStatus(GLISCOR);
        assertEquals(BigDecimal.valueOf(97.0), gliscorStatus.getHealth());
    }

    @Test
    void analyzeFieldHeal() {
        BattleEvent healthEvent = new BattleEvent("heal",
                List.of("p1a: in a groove", "100/100", "[from] Grassy Terrain"), null, null, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, RILLABOOM)
                .addPokemonStat(1, CORVIKNIGHT)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, RILLABOOM, RILLABOOM)
                .addPokemon(1, CORVIKNIGHT, "in a groove")
                .setTurnStartPokemon(2, RILLABOOM)
                .setTurnStartPokemon(1, CORVIKNIGHT)
                .setHealth(1, CORVIKNIGHT, BigDecimal.valueOf(99))
                .setFiled(new Field("Grassy Terrain", new EventTarget(2, RILLABOOM, RILLABOOM)))
                .build();
        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleContext);

        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(CORVIKNIGHT);
        PokemonBattleStat rillaboomStat = battleStat.playerStatList().get(1).getPokemonBattleStat(RILLABOOM);
        assertEquals(BigDecimal.valueOf(1.0), corviknightStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), corviknightStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(-1.0), rillaboomStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-1.0), rillaboomStat.getAttackValue());

        PokemonStatus corviknightStatus = battleContext.getPlayerStatusList().get(0).getPokemonStatus(CORVIKNIGHT);
        assertEquals(BigDecimal.valueOf(100.0), corviknightStatus.getHealth());
    }
    @Test
    void analyzeWishHeal() {
        BattleEvent healthEvent = new BattleEvent("heal",
                List.of("p2a: Hatterene", "100/100", "[from] move: Wish","[wisher] Alomomola"), null, null, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, HATTERENE)
                .addPokemonStat(2, ALOMOMOLA)
                .addPokemonStat(1, DARKRAI)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, HATTERENE, HATTERENE)
                .addPokemon(2, ALOMOMOLA, ALOMOMOLA)
                .addPokemon(1, DARKRAI, DARKRAI)
                .setTurnStartPokemon(2, ALOMOMOLA)
                .setTurnStartPokemon(1, DARKRAI)
                .setHealth(2, HATTERENE, BigDecimal.valueOf(44))
                .build();
        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleContext);

        PokemonBattleStat p1Stat = battleStat.playerStatList().get(0).getPokemonBattleStat(DARKRAI);
        PokemonBattleStat p2WisherStat = battleStat.playerStatList().get(1).getPokemonBattleStat(ALOMOMOLA);
        PokemonBattleStat hattereneStat = battleStat.playerStatList().get(1).getPokemonBattleStat(ALOMOMOLA);
        assertEquals(BigDecimal.valueOf(-56.0), p1Stat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-56.0), p1Stat.getAttackValue());
        assertEquals(BigDecimal.valueOf(56.0), p2WisherStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), p2WisherStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(0.0), hattereneStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(0.0), hattereneStat.getAttackValue());

        PokemonStatus pokemonStatus = battleContext.getPlayerStatusList().get(1).getPokemonStatus(HATTERENE);
        assertEquals(BigDecimal.valueOf(100.0), pokemonStatus.getHealth());
    }
}
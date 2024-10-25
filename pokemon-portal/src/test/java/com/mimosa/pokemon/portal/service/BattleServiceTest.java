/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.tour.TourBattle;
import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleServiceTest {
    public static final String EXIST_PLAYER_NAME = "lt111vz mimilimi";
    @Autowired
    BattleService battleService;

    @Autowired
    PlayerService playerService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void listPlayer() {
        PlayerRankDTO playerRankDTO = playerService.queryPlayerLadderRank(EXIST_PLAYER_NAME);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(playerRankDTO),
                () -> Assertions.assertNotEquals(0, playerRankDTO.getRank()),
                () -> Assertions.assertNotEquals(0, playerRankDTO.getElo()),
//                ()->Assertions.assertNotNull(playerRankDTO.getFormat()),
                () -> Assertions.assertNotNull(playerRankDTO.getInfoDate())
        );
    }

    @Test
    void test() {
        Battle tourBattle = new TourBattle();
        tourBattle.setBattleID("smogoutour123");
        mongoTemplate.save(tourBattle);
        List<TourBattle> tourBattles = mongoTemplate.find(new Query(), TourBattle.class);
        Assertions.assertFalse(tourBattles.isEmpty());
    }
}
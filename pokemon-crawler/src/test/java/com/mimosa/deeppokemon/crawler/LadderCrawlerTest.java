/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Ladder;
import com.mimosa.deeppokemon.entity.LadderRank;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.LadderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@SpringBootTest
@ContextConfiguration(classes = {LadderCrawlerTest.TestConfig.class, MongodbTestConfig.class})
class LadderCrawlerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "testLadderCrawler")
        public LadderCrawler ladderCrawler() {
            return new LadderCrawler("gen9ou", 1,
                    3, 1600, LocalDate.now().minusMonths(1), 60.0f);
        }
    }

    @Autowired
    @Qualifier("testLadderCrawler")
    LadderCrawler ladderCrawler;

    @Autowired
    BattleService battleSevice;

    @Autowired
    LadderService ladderService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void crawLadderRank() {
        LocalDate today = LocalDate.now();
        String exceptId = DateTimeFormatter.BASIC_ISO_DATE.format(today);
        Ladder ladder = null;
        try {
            ladder = ladderCrawler.crawLadderRank(false);
            Assertions.assertNotNull(ladder);
            Assertions.assertEquals(exceptId, ladder.getId());
            Assertions.assertEquals(today, ladder.getDate());
            Assertions.assertEquals("gen9ou", ladder.getFormat());
            Assertions.assertNotNull(ladder.getLadderRankList());
            Assertions.assertFalse(ladder.getLadderRankList().isEmpty());

            for (LadderRank ladderRank : ladder.getLadderRankList()) {
                Assertions.assertNotNull(ladderRank.getName());
                Assertions.assertNotEquals(0, ladderRank.getRank());
                Assertions.assertNotEquals(0, ladderRank.getElo());
                Assertions.assertNotEquals(0, ladderRank.getGxe());
            }
        } finally {
            if (ladder != null) {
                mongoTemplate.remove(ladder);
            }
        }
    }
}
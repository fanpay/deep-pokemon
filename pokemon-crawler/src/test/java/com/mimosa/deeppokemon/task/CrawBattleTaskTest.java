/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.task;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleReplayData;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.matcher.BattleMatcher;
import com.mimosa.deeppokemon.provider.FixedReplayProvider;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.commons.lang.time.StopWatch;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootTest
class CrawBattleTaskTest {
    private static final String EXIST_BATTLE_ID = "gen9ou-2171069120";
    protected static final int CRAW_PERIOD = 100;

    @Autowired
    BattleCrawler battleCrawler;

    @SpyBean
    BattleService battleService;

    @Value("classpath:api/battleReplay.json")
    Resource battleReplay;

    @Autowired
    private final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void call() throws IOException {
        BattleReplayData battleReplayData =
                OBJECT_MAPPER.readValue(battleReplay.getContentAsString(StandardCharsets.UTF_8), BattleReplayData.class);
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        Mockito.doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(battleService).save(Mockito.any(),
                Mockito.anyBoolean());
        Mockito.doNothing().when(battleService).insertTeam(Mockito.any());
        Mockito.doReturn(Collections.emptyList()).when(battleService).insertBattleStat(Mockito.any());
        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any(), Mockito.any(Class.class))).thenReturn(battleReplayData);
            List<Battle> battles =
                    new CrawBattleTask(new FixedReplayProvider(List.of("test-1")),
                            battleCrawler, null, battleService, false, 0).call();
            Assertions.assertFalse(battles.isEmpty());
            MatcherAssert.assertThat(battles, Matchers.everyItem(BattleMatcher.BATTLE_MATCHER));
        }
    }

    @Test
    void crawReplayException() {
        CrawBattleTask crawBattleTask = new CrawBattleTask(new MockExceptionReplayProvider(10), battleCrawler
                , null, battleService, false, 0);
        Assertions.assertDoesNotThrow(crawBattleTask::call);
        // mock has next exception
        crawBattleTask = new CrawBattleTask(new MockHasNextExceptionReplayProvider(10), battleCrawler
                , null, battleService, false, 0);
        Assertions.assertDoesNotThrow(crawBattleTask::call);
    }

    @Test
    void crawExistReplay() {
        Mockito.doReturn(Set.of(EXIST_BATTLE_ID)).when(battleService).getAllBattleIds();
        Mockito.doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(battleService).save(Mockito.any(),
                Mockito.anyBoolean());
        Mockito.doNothing().when(battleService).insertTeam(Mockito.any());
        Mockito.doReturn(Collections.emptyList()).when(battleService).insertBattleStat(Mockito.any());
        CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(List.of(EXIST_BATTLE_ID)),
                battleCrawler, null, battleService, false, 0);
        List<Battle> battles = crawBattleTask.call();
        Assertions.assertTrue(battles.isEmpty());
    }

    @Test
    void crawReplayWithCrawPeriod() {
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Mockito.doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(battleService).save(Mockito.any(),
                Mockito.anyBoolean());
        Mockito.doNothing().when(battleService).insertTeam(Mockito.any());
        Mockito.doReturn(Collections.emptyList()).when(battleService).insertBattleStat(Mockito.any());
        CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(List.of("test1", "test2", "test3")),
                new NoOpBattleCrawler(), null, battleService, false, CRAW_PERIOD);
        crawBattleTask.call();
        stopWatch.stop();
        Assertions.assertTrue(stopWatch.getTime() > 3 * CRAW_PERIOD);
    }


    @Test
    void crawHugeReplay() {
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        Mockito.doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(battleService).save(Mockito.any(),
                Mockito.anyBoolean());
        Mockito.doNothing().when(battleService).insertTeam(Mockito.any());
        Mockito.doReturn(Collections.emptyList()).when(battleService).insertBattleStat(Mockito.any());
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 110; ++i) {
            ids.add("test" + i);
        }
        CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(ids),
                new NoOpBattleCrawler(), null, battleService, false, 0);
        List<Battle> battles = crawBattleTask.call();
        Assertions.assertEquals(110, battles.size());
    }

    private static class MockExceptionReplayProvider implements ReplayProvider {
        protected int replayNumber;

        public MockExceptionReplayProvider(int replayNumber) {
            this.replayNumber = replayNumber;
        }

        @Override
        public ReplaySource next() {
            throw new RuntimeException("mock exception");
        }

        @Override
        public boolean hasNext() {
            return replayNumber-- == 0;
        }
    }


    private static class MockHasNextExceptionReplayProvider extends MockExceptionReplayProvider {

        public MockHasNextExceptionReplayProvider(int replayNumber) {
            super(replayNumber);
        }

        @Override
        public boolean hasNext() {
            throw new RuntimeException("mock exception");
        }
    }


    private static class NoOpBattleCrawler implements BattleCrawler {
        @Override
        public List<Battle> craw(ReplaySource replay) {
            return Collections.singletonList(new Battle(null));
        }
    }
}
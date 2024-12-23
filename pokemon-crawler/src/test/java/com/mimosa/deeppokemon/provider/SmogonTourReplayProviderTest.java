/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.crawler.SmogonTourWinPlayerExtractor;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.SmogonTourReplay;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmogonTourReplayProviderTest {
    @Value("classpath:api/2024WcopReplay.html")
    private Resource replayDocument;

    @Value("classpath:api/2024SplReplay.html")
    private Resource splReplayDocument;

    @ParameterizedTest
    @CsvSource(value = {"gen9ou", "gen8ou"})
    void Wcop2024(String format) throws IOException {
        String replayThreadUrl = "http1s://www.smogon" +
                ".com/forums/threads/the-world-cup-of-pok%C3%A9mon-2024-replays.3742226/";
        List<String> stageTitles = List.of("Qualifiers", "Qualifiers Round 2", "Round 1", "Quarterfinals", "Semifinals", "Finals");

        Document document = Jsoup.parse(replayDocument.getFile());
        SmogonTourReplayProvider smogonTourReplayProvider;

        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.doReturn(document).when(connection).get();
            SmogonTourWinPlayerExtractor winPlayerExtractor = Mockito.mock(SmogonTourWinPlayerExtractor.class);
            Mockito.doReturn(new TourPlayer("a", null, null)).when(winPlayerExtractor)
                    .getWinSmogonPlayer(Mockito.any(), Mockito.anyList());

            smogonTourReplayProvider = new SmogonTourReplayProvider("WCOP2024", replayThreadUrl,
                    format, stageTitles, winPlayerExtractor);
            assertTrue(smogonTourReplayProvider.hasNext());
        }
        List<String> exceptStageTitles = List.of("Qualifiers", "Qualifiers Round 2", "Round 1", "Round 1 TB",
                "Quarterfinals", "Semifinals", "Semifinals TB", "Finals");
        Map<String, Boolean> stageMap = new HashMap<>();
        for (String exceptStageTitle : exceptStageTitles) {
            stageMap.put(exceptStageTitle, false);
        }
        while (smogonTourReplayProvider.hasNext()) {
            List<Replay> replays = smogonTourReplayProvider.next().replayList();
            for (Replay replay : replays) {
                SmogonTourReplay smogonTourReplay = (SmogonTourReplay) replay;
                stageMap.put(smogonTourReplay.getStage(), true);
                assertNotNull(smogonTourReplay.getTourName());
                assertNotNull(smogonTourReplay.getTourPlayers());
                assertNotNull(smogonTourReplay.getId());
                assertNotNull(smogonTourReplay.getWinPlayer());
                for (var player : smogonTourReplay.getTourPlayers()) {
                    assertNotNull(player.getName());
                    assertNotNull(player.getTourPlayerId());
                    assertEquals(player.getName(), player.getName().trim().toLowerCase());
                }
            }
        }
        assertEquals(exceptStageTitles.size(), stageMap.size());
        for (var entry : stageMap.entrySet()) {
            if (entry.getKey().equals("Round 1 TB") && !format.equals("gen9ou")) {
                continue;
            }
            assertTrue(entry.getValue());
        }
    }
    @ParameterizedTest
    @CsvSource(value = {"gen9ou", "gen8ou"})
    void SplXv(String format) throws IOException {
        String replayThreadUrl = "http1s://www.smogon" +
                ".com/forums/threads/the-world-cup-of-pok%C3%A9mon-2024-replays.3742226/";
        List<String> stageTitles = List.of("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8", "Week 9",
                "Semifinals", "Finals");

        Document document = Jsoup.parse(splReplayDocument.getFile());
        SmogonTourReplayProvider smogonTourReplayProvider;

        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.doReturn(document).when(connection).get();
            SmogonTourWinPlayerExtractor winPlayerExtractor = Mockito.mock(SmogonTourWinPlayerExtractor.class);
            Mockito.doReturn(new TourPlayer("a", null, null)).when(winPlayerExtractor)
                    .getWinSmogonPlayer(Mockito.any(), Mockito.anyList());

            smogonTourReplayProvider = new SmogonTourReplayProvider("SPLXV", replayThreadUrl,
                    format, stageTitles, winPlayerExtractor);
            assertTrue(smogonTourReplayProvider.hasNext());
        }
        List<String> exceptStageTitles = format.equals("gen9ou") ?
                List.of("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8", "Week 9", "Semifinals", "Semifinals TB", "Finals")
                : List.of("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8", "Week 9", "Semifinals", "Finals");
        Map<String, Boolean> stageMap = new HashMap<>();
        for (String exceptStageTitle : exceptStageTitles) {
            stageMap.put(exceptStageTitle, false);
        }
        while (smogonTourReplayProvider.hasNext()) {
            List<Replay> replays = smogonTourReplayProvider.next().replayList();
            for (Replay replay : replays) {
                SmogonTourReplay smogonTourReplay = (SmogonTourReplay) replay;
                stageMap.put(smogonTourReplay.getStage(), true);
                assertNotNull(smogonTourReplay.getTourName());
                assertNotNull(smogonTourReplay.getTourPlayers());
                assertNotNull(smogonTourReplay.getId());
                assertNotNull(smogonTourReplay.getWinPlayer());
                for (var player : smogonTourReplay.getTourPlayers()) {
                    assertNotNull(player.getName());
                    assertNotNull(player.getTourPlayerId());
                    assertEquals(player.getName(), player.getName().trim().toLowerCase());
                }
            }
        }
        assertEquals(exceptStageTitles.size(), stageMap.size());
        for (var entry : stageMap.entrySet()) {
            assertTrue(entry.getValue());
        }
    }

}
/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.crawler.ReplayBattleCrawler;
import com.mimosa.deeppokemon.crawler.SmogonTourReplayBattleCrawler;
import com.mimosa.deeppokemon.crawler.SmogonTourWinPlayerExtractor;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.TourTeamGroupDetail;
import com.mimosa.deeppokemon.entity.tour.*;
import com.mimosa.deeppokemon.provider.OltTourReplayProvider;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.provider.SmogonTourReplayProvider;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TourService {
    private static final Logger log = LoggerFactory.getLogger(TourService.class);
    protected static final String SMOGON_PLAYER = "smogonPlayer";
    protected static final String WIN_SMOGON_PLAYER_NAME = "winSmogonPlayerName";
    protected static final String AVAGE_RATING = "avageRating";
    protected static final String TURN_COUNT = "turnCount";
    protected static final String TOUR_ID = "tourId";
    protected static final String FORMAT = "format";
    protected static final String TIER = "tier";
    protected static final String GEN_9_OU = "gen9ou";

    protected static final String WCOP_2024 = "wcop_2024";
    protected static final String WCOP_2024_FULL_TOUR_NAME = "The World Cup of Pokémon 2024";
    protected static final String WCOP_2024_REPLAY_URL = "https://www.smogon.com/forums/threads/the-world-cup-of-pok%C3%A9mon-2024-replays.3742226/";
    protected static final String WCOP_FORUMS_URL = "https://www.smogon.com/forums/forums/world-cup-of-pokemon.234/";
    protected static final List<String> WCOP_FORUMS_THREAD_SUFFIX_STAGES =
            List.of("Qualifiers", "Round 1", "Quarterfinals", "Semifinals", "Finals");
    protected static final List<String> WCOP_REPLAY_STAGES =
            List.of("Qualifiers", "Qualifiers Round 2", "Round 1", "Quarterfinals", "Semifinals", "Finals");


    protected static final String OLT_XI = "olt_xi";
    protected static final String OLT_XI_FULL_TOUR_NAME = "Smogon's Official Ladder Tournament XI";
    protected static final String OLT_FORUMS_URL =
            "https://www.smogon.com/forums/forums/official-ladder-tournament.465/";
    protected static final String OLT_XI_REPLAY_URL =
            "https://www.smogon.com/forums/threads/smogons-official-ladder-tournament-xi-replay-thread.3750361//";
    protected static final List<String> OLT_STAGES =
            List.of("Round 1", "Round 2", "Round 3", "Round 4", "Round 5", "Top 16",
                    "Quarterfinals", "Semifinals", "Finals");

    protected static final String SCL_IV = "SCL IV";
    protected static final String SMOGON_CHAMPIONS_LEAGUE_IV = "Smogon Champions League IV";
    protected static final String SCL_FORUMS_URL =
            "https://www.smogon.com/forums/forums/smogon-champions-league.453/";
    protected static final String SCL_IV_REPLAY_URL =
            "https://www.smogon.com/forums/threads/scl-iv-replays.3750816/";
    protected static final List<String> SCL_STAGES =
            List.of("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8", "Week 9",
                    "Semifinals", "Finals");

    protected static final String SPL_XV = "SPL XV";
    protected static final String SMOGON_PREMIER_LEAGUE_XV = "Smogon Premier League XV";
    protected static final String SPL_FORUMS_URL =
            "https://www.smogon.com/forums/forums/smogon-premier-league.130/";
    protected static final String SPL_XV_REPLAY_URL =
            "https://www.smogon.com/forums/threads/spl-xv-replays.3734444/";
    protected static final List<String> SPL_STAGES =
            List.of("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8", "Week 9",
                    "Semifinals", "Finals");

    private final BattleService battleService;
    private final MongoTemplate mongoTemplate;
    private final ReplayBattleCrawler replayBattleCrawler;

    public TourService(BattleService battleService, MongoTemplate mongoTemplate, ReplayBattleCrawler replayBattleCrawler) {
        this.battleService = battleService;
        this.mongoTemplate = mongoTemplate;
        this.replayBattleCrawler = replayBattleCrawler;
    }

    public List<Battle> crawTour(String tourName, String tourShortName, String format, ReplayProvider replayProvider) {
        SmogonTourReplayBattleCrawler crawler = new SmogonTourReplayBattleCrawler(replayBattleCrawler);
        CompletableFuture<List<Battle>> future = battleService.crawBattle(replayProvider, crawler, false);

        List<Battle> battles = future.join();

        if (!battles.isEmpty()) {
            updateTour(tourName, tourShortName, format);
            updatePlayerRecord(tourName, format);
            String teamGroupCollectionName = GEN_9_OU.equals(format) ? String.format("team_group_tour_%s", tourShortName)
                    : String.format("team_group_tour_%s_%s", tourShortName, format);
            String teamSetCollectionName = GEN_9_OU.equals(format) ? String.format("team_set_tour_%s", tourShortName)
                    : String.format("team_set_tour_%s_%s", tourShortName, format);
            battleService.updateTeam(new TourTeamGroupDetail(teamGroupCollectionName,
                    teamSetCollectionName, tourName, format));
        } else {
            log.error("craw empty battle of {}", tourName);
        }

        return battles;
    }

    @RegisterReflectionForBinding(Tour.class)
    public void updateTour(String tourName, String tourShortName, String format) {
        Criteria criteria = Criteria.where(TOUR_ID).is(tourName)
                .and(TIER).is(format);
        Tour tour = mongoTemplate.findById(tourName, Tour.class);
        List<String> tierPlayers = mongoTemplate.findDistinct(new Query(criteria), "player.name", TourTeam.class, String.class);
        Query tourQuery = new Query(Criteria.where(TOUR_ID).is(tourName));
        List<String> stages = mongoTemplate.findDistinct(tourQuery, "stage", TourTeam.class,
                String.class);
        tourQuery.with(Sort.by("battleDate"));
        LocalDate firstBattleDate = mongoTemplate.findOne(tourQuery, TourTeam.class).getBattleDate().toLocalDate();
        if (tour == null) {
            tour = new Tour();
            tour.setId(tourName);
            tour.setShortName(tourShortName);
            tour.setTires(Collections.singletonList(format));
            tour.setTierPlayers(Collections.singletonMap(format, tierPlayers));
            tour.setStages(stages);
            tour.setStartDate(firstBattleDate);
            mongoTemplate.insert(tour);
        } else {
            Set<String> tiers = new HashSet<>(tour.getTires());
            Map<String, List<String>> tierplayersMap = new HashMap<>();

            tiers.add(format);
            if (tour.getTierPlayers() != null) {
                tierplayersMap.putAll(tour.getTierPlayers());
            }
            tierplayersMap.put(format, tierPlayers);
            tour.setShortName(tourShortName);
            tour.setTires(tiers.stream().toList());
            tour.setStages(stages);
            tour.setStartDate(firstBattleDate);
            tour.setTierPlayers(tierplayersMap);
            mongoTemplate.save(tour);
        }
    }

    @RegisterReflectionForBinding({TourBattle.class, TourPlayer.class})
    public List<TourPlayerRecord> updatePlayerRecord(String tourName, String format) {
        Criteria criteria = Criteria.where(TOUR_ID).is(tourName)
                .and(FORMAT).is(format);
        Query query = new Query(criteria);
        query.fields().include(SMOGON_PLAYER, WIN_SMOGON_PLAYER_NAME, AVAGE_RATING, TURN_COUNT);
        List<TourBattle> tourBattles = mongoTemplate.find(query, TourBattle.class);
        Map<String, TourPlayerRecord> recordMap = new HashMap<>();
        for (TourBattle tourBattle : tourBattles) {
            if (tourBattle.getSmogonPlayer() == null || tourBattle.getWinSmogonPlayerName() == null) {
                log.error("can find smogon player or winner in {}", tourBattle.getBattleID());
                continue;
            }

            for (TourPlayer player : tourBattle.getSmogonPlayer()) {
                String tourPlayerId = String.format("%s_%s_%s", tourName, format, player.getName());
                TourPlayerRecord playerRecord = recordMap.computeIfAbsent(player.getName(),
                        k -> new TourPlayerRecord(tourPlayerId, player.getName(), tourName, format, player.getTeam()));

                playerRecord.setTotal(playerRecord.getTotal() + 1);
                if (StringUtils.equals(player.getName(), tourBattle.getWinSmogonPlayerName())) {
                    playerRecord.setWin(playerRecord.getWin() + 1);
                } else {
                    playerRecord.setLoss(playerRecord.getLoss() + 1);
                }
                playerRecord.setWinDif(playerRecord.getWin() - playerRecord.getLoss());
                playerRecord.setWinRate((float) playerRecord.getWin() / (float) playerRecord.getTotal());
            }
        }
        List<TourPlayerRecord> tourPlayerRecords = new ArrayList<>(recordMap.values());
        mongoTemplate.remove(query, TourPlayerRecord.class);
        mongoTemplate.insertAll(tourPlayerRecords);
        return tourPlayerRecords;
    }

    @CacheEvict(value = {"tours", "teamGroup", "teamInfo"}, allEntries = true)
    public List<Battle> crawWcop2024(String format) {
        SmogonTourWinPlayerExtractor winPlayerExtractor = new SmogonTourWinPlayerExtractor(WCOP_FORUMS_URL,
                WCOP_2024_FULL_TOUR_NAME, WCOP_FORUMS_THREAD_SUFFIX_STAGES);
        SmogonTourReplayProvider provider = new SmogonTourReplayProvider(WCOP_2024_FULL_TOUR_NAME, WCOP_2024_REPLAY_URL,
                format, WCOP_REPLAY_STAGES, winPlayerExtractor);
        return crawTour(WCOP_2024_FULL_TOUR_NAME, WCOP_2024, format, provider);
    }

    @CacheEvict(value = {"tours", "teamGroup", "teamInfo"}, allEntries = true)
    public List<Battle> crawOltXI() {
        SmogonTourWinPlayerExtractor winPlayerExtractor = new SmogonTourWinPlayerExtractor(OLT_FORUMS_URL,
                OLT_XI_FULL_TOUR_NAME, OLT_STAGES);
        OltTourReplayProvider provider = new OltTourReplayProvider(OLT_XI_FULL_TOUR_NAME, OLT_XI_REPLAY_URL,
                GEN_9_OU, new HashSet<>(OLT_STAGES), winPlayerExtractor);
        return crawTour(OLT_XI_FULL_TOUR_NAME, OLT_XI, GEN_9_OU, provider);
    }

    @CacheEvict(value = {"tours", "teamGroup", "teamInfo"}, allEntries = true)
    public List<Battle> crawSclIv() {
        SmogonTourWinPlayerExtractor winPlayerExtractor = new SmogonTourWinPlayerExtractor(SCL_FORUMS_URL,
                SMOGON_CHAMPIONS_LEAGUE_IV, SCL_STAGES);
        SmogonTourReplayProvider provider = new SmogonTourReplayProvider(SMOGON_CHAMPIONS_LEAGUE_IV, SCL_IV_REPLAY_URL,
                GEN_9_OU, SCL_STAGES, winPlayerExtractor);
        return crawTour(SMOGON_CHAMPIONS_LEAGUE_IV, SCL_IV, GEN_9_OU, provider);
    }


    @CacheEvict(value = {"tours", "teamGroup", "teamInfo"}, allEntries = true)
    public List<Battle> crawSplXv(String format) {
        SmogonTourWinPlayerExtractor winPlayerExtractor = new SmogonTourWinPlayerExtractor(SPL_FORUMS_URL,
                SMOGON_PREMIER_LEAGUE_XV, SPL_STAGES);
        SmogonTourReplayProvider provider = new SmogonTourReplayProvider(SMOGON_PREMIER_LEAGUE_XV, SPL_XV_REPLAY_URL,
                format, SPL_STAGES, winPlayerExtractor);
        return crawTour(SMOGON_PREMIER_LEAGUE_XV, SPL_XV, format, provider);
    }
}
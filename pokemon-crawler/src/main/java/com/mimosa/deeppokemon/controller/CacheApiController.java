/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.service.CacheService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/cache")
public class CacheApiController {
    private final CacheService cacheService;

    public CacheApiController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/battle/delete")
    public boolean deleteRankAndBattle() {
        cacheService.clearRank();
        cacheService.clearPlayerBattle();
        return true;
    }

    @PostMapping("/team/delete")
    public boolean deleteTeamGroup() {
        cacheService.clearTeamGroup();
        return true;
    }

    @PostMapping("/stat/delete")
    public boolean deleteStat() {
        cacheService.clearMonthlyStat();
        return true;
    }

    @PostMapping("/deleteAll")
    public boolean deleteAll() {
        cacheService.clearAll();
        return true;
    }
}
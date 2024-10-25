/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.dto;

import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import com.mimosa.deeppokemon.entity.tour.TourPlayerRecord;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record BattleTeamDto(String battleId, LocalDate battleDate, Integer rating, String playerName, String tourId,
                            String stage, TourPlayer player, TourPlayerRecord playerRecord, List<Pokemon> pokemons) implements Serializable {
}
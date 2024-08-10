/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.entity.*;
import org.bson.types.Binary;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeamServiceTest {
    @SpyBean
    private TeamService teamService;

    @Test
    void queryNeedUpdateTeamGroup() {
        TeamSet teamSetA = new TeamSet(new Binary("testA".getBytes()), "gen9ou", 1, null);
        TeamSet teamSetB = new TeamSet(new Binary("testB".getBytes()), "gen9ou", 3, null);
        Mockito.doReturn(List.of(teamSetA, teamSetB)).when(teamService).getTeamSets(Mockito.any());
        List<TeamGroup> teamGroups = new ArrayList<>();
        TeamGroup teamGroupA = new TeamGroup("testA".getBytes(), null, 0, 1, 0, null, null, null, null);
        TeamGroup teamGroupB = new TeamGroup("testB".getBytes(), null, 0, 5, 0, null, null, null, null);
        TeamGroup teamGroupC = new TeamGroup("testC".getBytes(), null, 0, 1, 0, null, null, null, null);
        teamGroups.add(teamGroupA);
        teamGroups.add(teamGroupB);
        teamGroups.add(teamGroupC);
        List<Binary> binaries = new ArrayList<>(teamService.queryNeedUpdateTeamGroup(teamGroups));
        assertEquals(2, binaries.size());
        assertEquals("testB", new String(binaries.get(0).getData()));
        assertEquals("testC", new String(binaries.get(1).getData()));
    }

    @Test
    void buildTeamSet() {
        List<Pokemon> pokemonsA = Collections.singletonList(buildPokemon("Clefable", "Magic gurad", "Leftovers",
                List.of("Stealth Rock")));
        List<Pokemon> pokemonsB = Collections.singletonList(buildPokemon("Clefable", "Magic gurad", "Leftovers",
                List.of("Stealth Rock", "MoonBlast")));
        List<Pokemon> pokemonsC = Collections.singletonList(buildPokemon("Clefable", "Unware", "Heavy-Duty Boots",
                List.of("Stealth Rock", "Wish", "MoonBlast")));
        BattleTeam battleTeamA = new BattleTeam("1", "1", "1".getBytes(), null, null,
                0, null, "gen9ou", pokemonsA, null);
        BattleTeam battleTeamB = new BattleTeam("1", "1", "1".getBytes(), null, null,
                0, null, "gen9ou", pokemonsB, null);
        BattleTeam battleTeamC = new BattleTeam("1", "1", "1".getBytes(), null, null,
                0, null, "gen9ou", pokemonsC, null);

        TeamGroup teamGroup = new TeamGroup("1".getBytes(), "gen9ou", 3, 3, 0,
                null, null, null, List.of(battleTeamA, battleTeamB, battleTeamC));
        TeamSet teamSet = teamService.buildTeamSet(teamGroup);
        assertEquals("1", new String(teamSet.id().getData()));
        assertEquals("gen9ou", teamSet.tier());
        assertEquals(3, teamSet.replayNum());
        assertEquals(1, teamSet.pokemons().size());
        PokemonBuildSet pokemonBuildSet = teamSet.pokemons().get(0);
        assertEquals("Clefable", pokemonBuildSet.name());
        assertEquals(3, pokemonBuildSet.moves().size());
        assertEquals("Stealth Rock", pokemonBuildSet.moves().get(0));
        assertEquals("MoonBlast", pokemonBuildSet.moves().get(1));
        assertEquals("Wish", pokemonBuildSet.moves().get(2));


        assertEquals(2, pokemonBuildSet.items().size());
        assertEquals("Leftovers", pokemonBuildSet.items().get(0));
        assertEquals("Heavy-Duty Boots", pokemonBuildSet.items().get(1));

        assertEquals(2, pokemonBuildSet.abilities().size());
        assertEquals("Magic gurad", pokemonBuildSet.abilities().get(0));
        assertEquals("Unware", pokemonBuildSet.abilities().get(1));

    }

    private Pokemon buildPokemon(String name, String ability, String item, List<String> move) {
        Pokemon pokemon = new Pokemon(name);
        pokemon.setMoves(new HashSet<>(move));
        pokemon.setItem(item);
        pokemon.setAbility(ability);
        return pokemon;
    }
}
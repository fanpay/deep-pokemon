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

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class PokemonTypeTagProviderTest {
    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Autowired
    private PokemonTypeTagProvider pokemonTypeTagProvider;

    @Test
    void tag() throws IOException {
        List<PokemonInfo> pokemonInfoList = pokemonInfoCrawler.craw();
        for (int i = 0; i < pokemonInfoList.size(); i++) {
            PokemonInfo pokemonInfo =  pokemonInfoList.get(i);
            pokemonTypeTagProvider.tag(pokemonInfo, null);
            if ("OU".equals(pokemonInfo.getTier()) || "UU".equals(pokemonInfo.getTier())) {
                System.out.println(pokemonInfo.getName() + pokemonInfo.getTypes() + ':'
                        + pokemonInfo.getTags());
            }
        }
    }
}
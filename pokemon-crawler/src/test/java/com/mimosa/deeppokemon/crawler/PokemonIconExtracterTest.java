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

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class PokemonIconExtracterTest {
    public static final Path iconPath;

    public static final Path pokemonIconIndexPath;

    public static final Path itemPath;

    public static final Path itemIndexPath;

    static {
        try {
            pokemonIconIndexPath = ResourceUtils.getFile("classpath:icon/pokemonIconIndex.json").toPath();
            iconPath = ResourceUtils.getFile("classpath:icon/pokemonicons-sheet_v16.png").toPath();
            itemPath = ResourceUtils.getFile("classpath:icon/itemicons-sheet.png").toPath();
            itemIndexPath = ResourceUtils.getFile("classpath:icon/itemInfo.json").toPath();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
    void extract() throws IOException {
        try {
            PokemonInfoCrawler pokemonInfoCrawler = new PokemonInfoCrawlerImp();
            List<PokemonInfo> pokemonInfos = pokemonInfoCrawler.craw();
            PokemonIconExtracter pokemonIconExtracter = new PokemonIconExtracter(iconPath, pokemonIconIndexPath,
                    pokemonInfos);
            pokemonIconExtracter.extract();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    void extractItem() throws IOException {
        try {
            ItemIconExtracter itemIconExtracter = new ItemIconExtracter(itemPath, itemIndexPath);
            itemIconExtracter.extract();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
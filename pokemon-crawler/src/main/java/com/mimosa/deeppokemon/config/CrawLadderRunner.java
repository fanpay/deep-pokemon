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

package com.mimosa.deeppokemon.config;

import com.mimosa.deeppokemon.crawler.LadderCrawler;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.LadderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Profile("startCraw")
public class CrawLadderRunner{
    @Autowired
    LadderCrawler ladderCrawler;

    @Autowired
    BattleService battleSevice;

    @Autowired
    LadderService ladderService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledConfig.class);

    /**
     * 应用启动后爬取排行榜进行初始化统计
     */
    @EventListener(value = ApplicationStartedEvent.class)
    public void crawLadder() throws Exception {
        log.info(String.format("craw  " +
                        "start: format:%s pageLimit:%d rankLimit:%d eloLimit:%d gxeLimit:%f dateLimit:%tF",
                ladderCrawler.getFormat(), ladderCrawler.getPageLimit(), ladderCrawler.getRankMoreThan(),
                ladderCrawler.getMinElo(), ladderCrawler.getMinGxe(), ladderCrawler.getDateAfter()));
        ladderCrawler.crawLadder();
    }
}
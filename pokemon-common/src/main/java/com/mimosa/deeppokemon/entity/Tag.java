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

package com.mimosa.deeppokemon.entity;

/**
 * @program: deep-pokemon
 * @description: 宝可梦分类标签
 * @author: mimosa
 * @create: 2020//10//18
 */
public enum Tag {
    //攻受标签
    STAFF("STAFF"), BALANCE("BALANCE"), BALANCE_ATTACK("BALANCE_ATTACK"), BALANCE_STAFF("BALANCE_STAFF"),
    ATTACK("ATTACK"), WEAK("WEAK"),

    ATTACK_SET("ATTACK_SET"), DEFENSE_SET("DEFENSE_SET"), ATTACK_MIX_SET("ATTACK_MIX_SET"),
    DEFENSE_MIX_SET("DEFENSE_MIX_SET"), ATTACK_BULK_SET("ATTACK_BULK_SET"), DEFENSE_BULK_SET("DEFENSE_BULK_SET"),
    BALANCE_SET("BALANCE_SET"), BALANCE_BULK_SET("BALANCE_BULK_SET"),

    //使用率标签
    UNPOPULAR("UNPOPULAR"), POPULAR("POPULAR"), COMMON("COMMON"),

    //种族标签
    PRETTY_ATTACKSTATS("PRETTY_ATTACKSTATS"), OUTSTANDING_ATTACKSTATS("OUTSTANDING_ATTACKSTATS"),
    EXCELLENT_ATTACKSTATS("EXCELLENT_ATTACKSTATS"), GOOD_ATTACKSTATS("GOOD_ATTACKSTATS"),
    NORMAL_ATTACKSTATS("NORMAL_ATTACKSTATS"), BAD_ATTACKSTATSS("BAD_ATTACKSTATS"),

    PRETTY_DEFENCESTATS("PRETTY_DEFENCESTATS"), EXCELLENT_DEFENCESTATS("EXCELLENT_DEFENCESTATS"),
    GOOD_DEFENCESTATS("GOOD_DEFENCESTATS"), NORMAL_DEFENCESTATS("NORMAL_DEFENCESTATS"),
    BAD_DEFENCESTATS("BAD_DEFENCESTATS"),

    PRETTY_SPASTATS("PRETTY_SPASTATS"), OUTSTANDING_SPASTATS("OUTSTANDING_SPASTATS"), EXCELLENT_SPASTATS(
            "EXCELLENT_SPASTATS"), GOOD_SPASTATS("GOOD_SPASTATS"),
    NORMAL_SPASTATS("NORMAL_SPASTATS"), BAD_SPASTATS("BAD_SPASTATS"),

    PRETTY_SPDSTATS("PRETTY_SPDSTATS"), EXCELLENT_SPDSTATS("EXCELLENT_SPDSTATS"), GOOD_SPDSTATS("GOOD_SPDSTATS"),
    NORMAL_SPDSTATS("NORMAL_SPDSTATS"), BAD_SPDSTATS("BAD_SPDSTATS"),

    PRETTY_SPESTATS("PRETTY_SPESTATS"), EXCELLENT_SPESTATS("EXCELLENT_SPESTATS"), GOOD_SPESTATS("GOOD_SPESTATS"),
    NORMAL_SPESTATS("NORMAL_SPESTATS"), BAD_SPESTATS("BAD_SPESTATS"),

    //属性标签
    TYPE_BAD("TYPE_BAD"), TYPE_NORMAL("TYPE_NORMAL"), TYPE_GOOD("TYPE_GOOD"), TYPE_EXCELLENT("TYPE_EXCELLENT"),
    TYPE_PRETTY("TYPE_PRETTY"), TYPE_LITTLEWEAK("TYPE_LITTLEWEAK"), TYPE_NORMALWEAK("TYPE_NORMALWEAK"),
    TYPE_MANYWEAK("TYPE_MANYWEAK"),


    //特性标签
    ABILITY_ATTACK_BAD("ABILITY_ATTACK_BAD"),
    ABILITY_ATTACK_GOOD("ABILITY_ATTACK_GOOD"),
    ABILITY_ATTACK_PRETTY("ABILITY_ATTACK_PRETTY"),


    ABILITY_DEFENCE_BAD("ABILITY_DEFENCE_BAD"),
    ABILITY_DEFENCE_GOOD("ABILITY_DEFENCE_GOOD"),
    ABILITY_DEFENCE_PRETTY("ABILITY_DEFENCE_PRETTY"),

    //场地特性标签
    ABILITY_WEATHER("ABILITY_WEATHER");


    String name;

    Tag(String tag) {
        this.name = tag;
    }

    public String getName() {
        return name;
    }
}
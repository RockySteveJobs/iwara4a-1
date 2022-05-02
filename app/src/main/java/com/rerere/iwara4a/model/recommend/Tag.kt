package com.rerere.iwara4a.model.recommend

object TagParser {
    fun parse(text: String): Set<String> {
        return tagDictionary
            .filter { entry ->
                entry.second.any {
                    text.contains(it, true)
                }
            }
            .map { it.first }
            .toSet()
    }
}

// 标签词典
private val tagDictionary = listOf(
    // 特殊标签
    "cloth_solver" to setOf("布料", "解算"),
    // 角色来源
    "genshin" to setOf("原神", "genshin", "甘雨", "刻晴", "优菈", "丘丘人", "雷电", "八重神子", "琴", "芭芭拉"),
    "impact3" to setOf("崩坏", "幽兰黛尔", "芽衣", "雅琪娜", "八重樱", "明日香", "梅比乌斯", "布洛妮娅", "鸭鸭", "呆鹅", "希儿", "丽塔", "板鸭"),

    "miku" to setOf("miku", "初音"),
    "haku" to setOf("haku", "弱音"),
    "tianyi" to setOf("天依"),

    // XP
    "insect" to setOf("虫", "insect"),
    "futa" to setOf("futa", "扶她"),
    "foot_job" to setOf("foot job", "footjob", "足交"),
    "beads" to setOf("beads", "拉珠"),

    // 服饰
    "black_stocking" to setOf("黑丝", ),
    "white_stocking" to setOf("白丝"),
    "cheongsam" to setOf("cheongsam", "旗袍"),
    "swimsuit" to setOf("泳装", "swimsuit", "水着"),
    "high_heels" to setOf("高跟", "heels"),
)
package io.sebi.twemojiamazing

import com.beust.klaxon.Klaxon
import java.net.URL

object EmojisDotJsonProvider {
    fun getEmojisDotJson(): List<Emoji> {
        val json = URL("https://unpkg.com/emoji.json/emoji.json").readText()
        val jsonEmojis = Klaxon().parseArray<Emoji>(json) ?: error("Couldn't parse emoji.json.")
        return jsonEmojis + Emoji("e50a", "", "shibuya") // https://acelewis.com/blog/the-emoji-that-exists-but-is-not-real
    }
}
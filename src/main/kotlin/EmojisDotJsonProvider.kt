package io.sebi.twemojiamazing

import kotlinx.serialization.decodeFromString
import java.net.URL

object EmojisDotJsonProvider {
    fun getEmojisDotJson(): List<Emoji> {
        val emojiJson = URL("https://unpkg.com/emoji.json/emoji.json").readText()
        val jsonEmojis = json.decodeFromString<List<Emoji>>(emojiJson)
        return jsonEmojis + Emoji(
            "e50a",
            "",
            "shibuya"
        ) // https://acelewis.com/blog/the-emoji-that-exists-but-is-not-real
    }
}
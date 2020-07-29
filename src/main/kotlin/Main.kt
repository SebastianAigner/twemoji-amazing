package io.sebi.twemojiamazing

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import kotlinx.coroutines.runBlocking

val client = HttpClient(Apache) {
    engine {
        customizeClient {
            setMaxConnTotal(50)
            connectionRequestTimeout = -1
        }
    }
}

fun main() {
    val allTwemojis = TwemojiProvider.getTwemojiCodePoints()
    val emojisDotJson = EmojisDotJsonProvider.getEmojisDotJson()

    val joined = allTwemojis.map { twemojiCodepoint ->
        (emojisDotJson.firstOrNull { it.normalizedCodes == twemojiCodepoint }
                ?: CharacterResolver.resolveCodepoints(twemojiCodepoint)) //resolve emoji here
    }
    val validatedEmojis = runBlocking {
        joined.asyncFilter {
            it.isTwemojiAvailable()
        }
    }
    println(validatedEmojis.count())
    CssFileWriter.writeEmojisToFile(validatedEmojis, "twemoji-amazing.css")
}

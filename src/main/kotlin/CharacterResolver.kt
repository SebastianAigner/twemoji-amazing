package io.sebi.twemojiamazing

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

object CharacterResolver {
    private val regex = """<td>([^<]+)</td>""".toRegex()

    fun resolveCodepoints(twemojiCodepoint: String): Emoji {
        val emojiParts = twemojiCodepoint.split("-")
        println("Resolving ${twemojiCodepoint}...")
        val humanReadbleParts = emojiParts.map { resolveUnicodeCharacter(it) }.map {
            humanReadableNameMap[it] ?: it
        }.filterNot { it.isBlank() }
        val emojiName = humanReadbleParts.joinToString("-")
        println(emojiName)
        return Emoji(twemojiCodepoint, "", emojiName)
    }

    private fun resolveUnicodeCharacter(codepoint: String): String {
        val text = runBlocking {
            fileformatClient.get("https://www.fileformat.info/info/unicode/char/$codepoint/index.htm").bodyAsText()
        }
        val lines = text.lines()
        val characterNameIndex = lines.indexOfFirst { it.contains("<td>Name</td>") }
        val nameTableRow = lines[characterNameIndex + 1]
        val (title) = regex.find(nameTableRow)?.destructured
                ?: error("Couldn't find title for $codepoint. Please review:\n$text")
        return title
    }

    private val fileformatClient = HttpClient(Apache) {
        engine {
            customizeClient {
                setMaxConnTotal(1)
                connectionRequestTimeout = -1
            }
        }
    }

    private val humanReadableNameMap = mapOf(
            "EMOJI MODIFIER FITZPATRICK TYPE-1-2" to "light-skin-tone",
            "EMOJI MODIFIER FITZPATRICK TYPE-3" to "medium-light-skin-tone",
            "EMOJI MODIFIER FITZPATRICK TYPE-4" to "medium-skin-tone",
            "EMOJI MODIFIER FITZPATRICK TYPE-5" to "medium-dark-skin-tone",
            "EMOJI MODIFIER FITZPATRICK TYPE-6" to "dark-skin-tone",
            "ZERO WIDTH JOINER" to "",
            "CHRISTMAS TREE" to "christmas",
            "VARIATION SELECTOR-16" to "",
            "MALE SIGN" to "male",
            "FEMALE SIGN" to "female"
    )
}


package io.sebi.twemojiamazing

import com.beust.klaxon.Klaxon
import java.io.File
import java.net.URL

fun twemojiUrl(unicode: String) = "https://twemoji.maxcdn.com/2/svg/$unicode.svg"

data class Emoji(val codes: String, val name: String)

fun main(args: Array<String>) {
    val preamble = File("preamble.css")
    val preambleLines = preamble.readLines()
    val json = URL("https://unpkg.com/emoji.json/emoji.json").readText()
    val result = Klaxon().parseArray<Emoji>(json) ?: listOf()
    val new = result.filter {
        it.name.all { it.isLetter() || it == ' ' }
    }.map {
        Emoji(it.codes.toLowerCase().split(" ").joinToString("-"), it.name.toLowerCase().split(" ").joinToString("-"))
    }
    val cssClasses = new.map {
        """
            .twa-${it.name} {
                background-image: url("${twemojiUrl(it.codes)}")
            }
        """.trimIndent()
    }
    println("Generated a total of ${new.count()} emoji CSS classes.")
    File("twemoji-amazing.css").printWriter().use { out ->
        preambleLines.forEach {
            out.println(it)
        }
        out.println("""

            /* Generated a total of ${new.count()} emoji CSS classes. */

        """.trimIndent())
        cssClasses.forEach {
            out.println(it)
        }
    }
}
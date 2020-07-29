package io.sebi.twemojiamazing

import java.io.File

object CssFileWriter {
    fun writeEmojisToFile(emojis: List<Emoji>, filename: String) {
        File(filename).printWriter().use { out ->
            out.println(File("preamble.css").readText().replace("##CLASSCOUNT", emojis.count().toString()))
            emojis.sortedBy { it.normalizedName }.forEach {
                out.println(it.cssClass)
            }
        }
    }
}
package io.sebi.twemojiamazing

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

data class Emoji(val codes: String, val char: String, val name: String) {
    val normalizedName: String = name
            .lowercase()
            .replace("#", "number")
            .replace("*", "star")
            .replace("\\W+".toRegex(), "-") // replace all groups of non-word characters with dashes
            .replace("-*\\z".toRegex(), "") // trim dashes at the end of the string (e.g. converted parentheses)

    val normalizedCodes: String = codes.removePrefix("00")
            .lowercase()
            .replace(" ", "-")

    private val twemojiUrl: String = "https://twemoji.maxcdn.com/v/latest/svg/$normalizedCodes.svg"

    val cssClass: String = """
            .twa-${normalizedName} {
                background-image: url("$twemojiUrl")
            }
        """.trimIndent()

    suspend fun isTwemojiAvailable(): Boolean {
        return client.head<HttpStatement>(twemojiUrl).execute {
            it.status == HttpStatusCode.OK
        }
    }
}
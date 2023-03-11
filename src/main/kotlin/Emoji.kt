package io.sebi.twemojiamazing

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
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

    private val twemojiUrl: String = "https://cdn.jsdelivr.net/gh/twitter/twemoji@master/assets/svg/$normalizedCodes.svg"

    val cssClass: String = """
            .twa-${normalizedName} {
                background-image: url("$twemojiUrl")
            }
        """.trimIndent()

    suspend fun isTwemojiAvailable(): Boolean {
        return client.head(twemojiUrl).status == HttpStatusCode.OK
    }
}
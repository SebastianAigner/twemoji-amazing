package io.sebi.twemojiamazing

import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.http.*

data class Emoji(val codes: String, val char: String, val name: String) {
    val normalizedName: String = name
            .toLowerCase()
            .replace("\\W+".toRegex(), "-") // replace all groups of non-word characters with dashes
            .replace("-*\\z".toRegex(), "") // trim dashes at the end of the string (e.g. converted parentheses)

    val normalizedCodes: String = codes.removePrefix("00")
            .toLowerCase()
            .replace(" ", "-")

    val twemojiUrl: String = "https://twemoji.maxcdn.com/v/latest/svg/$normalizedCodes.svg"

    val cssClass: String = """
            .twa-${normalizedName} {
                background-image: url("$twemojiUrl")
            }
        """.trimIndent()

    suspend fun isTwemojiAvailable(): Boolean {
        return client.head<HttpResponse>(twemojiUrl).status == HttpStatusCode.OK
    }
}
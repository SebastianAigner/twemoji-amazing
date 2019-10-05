package io.sebi.twemojiamazing

import com.beust.klaxon.Klaxon
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.head
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URL

data class Emoji(val codes: String, val char: String, val name: String) {
    val normalizedName: String
        get() {
            return name
                    .toLowerCase()
                    .replace("\\W+".toRegex(), "-") // replace all groups of non-word characters with dashes
                    .replace("-*\\z".toRegex(), "") // trim dashes at the end of the string (e.g. converted parentheses)
        }

    val normalizedCodes: String
        get() {
            return codes
                    .toLowerCase()
                    .replace(" ", "-")
        }

    val twemojiUrl: String
        get() {
            return "https://twemoji.maxcdn.com/v/latest/svg/$normalizedCodes.svg"
        }

    fun toCssClass(): String {
        return """
            .twa-${normalizedName} {
                background-image: url("$twemojiUrl")
            }
        """.trimIndent()
    }

    suspend fun isTwemojiAvailable(): Boolean {
        return client.head<HttpResponse>(twemojiUrl).status == HttpStatusCode.OK
    }
}

val client = HttpClient(Apache) {
    engine {
        customizeClient {
            setMaxConnTotal(50)
            connectionRequestTimeout = -1
        }
    }
}

fun main() {
    val json = URL("https://unpkg.com/emoji.json/emoji.json").readText()
    val emojis = Klaxon().parseArray<Emoji>(json) ?: listOf()

    println("Got a total of ${emojis.count()} emojis.")
    println("Validating... (This might take a while)")

    val validatedEmojis = runBlocking {
        emojis.asyncFilter {
            it.isTwemojiAvailable()
        }
    }

    println("Validation done.")
    println("Validated ${validatedEmojis.count()} emojis (out of ${emojis.count()}).")

    // Write CSS preamble and CSS classes to file

    File("twemoji-amazing.css").printWriter().use { out ->
        out.println(File("preamble.css").readText().replace("##CLASSCOUNT", validatedEmojis.count().toString()))
        validatedEmojis.forEach {
            out.println(it.toCssClass())
        }
    }
}

suspend fun <T> List<T>.asyncFilter(transform: suspend (T) -> Boolean): List<T> {
    return map { it to GlobalScope.async { transform(it) } }
            .filter { (_, transformResult) ->
                transformResult.await()
            }
            .map {
                it.first
            }
}
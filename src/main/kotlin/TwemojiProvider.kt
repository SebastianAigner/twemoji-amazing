package io.sebi.twemojiamazing

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

object TwemojiProvider {

    @Serializable
    class Response(val tree: List<RepositoryFile>)
    @Serializable
    data class RepositoryFile(val path: String, val type: String, val url: String)

    private fun getRepositoryFiles(url: String): List<RepositoryFile> {
        val response = runBlocking {
            client.get(url).bodyAsText()
        }

        return json.decodeFromString<Response>(response).tree
    }

    fun getTwemojiCodePoints(): List<String> {
        // https://api.github.com/repos/twitter/twemoji/contents/assets/svg
        val masterTree = getRepositoryFiles("https://api.github.com/repos/twitter/twemoji/git/trees/master")
        val assetsTree = getRepositoryFiles(masterTree.first { it.path == "assets" }.url)
        val svgTree = getRepositoryFiles(assetsTree.first { it.path == "svg" }.url)
        return svgTree.map { it.path.removeSuffix(".svg") }
    }
}
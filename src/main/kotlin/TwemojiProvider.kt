package io.sebi.twemojiamazing

import com.beust.klaxon.Klaxon
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking

object TwemojiProvider {
    class Response(val tree: Array<RepositoryFile>)
    data class RepositoryFile(val path: String, val type: String, val url: String)

    private fun getRepositoryFiles(url: String): Array<RepositoryFile> {
        val response = runBlocking {
            client.get<String>(url)
        }
        return Klaxon().parse<Response>(response)!!.tree
    }

    fun getTwemojiCodePoints(): List<String> {
        // https://api.github.com/repos/twitter/twemoji/contents/assets/svg
        val masterTree = getRepositoryFiles("https://api.github.com/repos/twitter/twemoji/git/trees/master")
        val assetsTree = getRepositoryFiles(masterTree.first { it.path == "assets" }.url)
        val svgTree = getRepositoryFiles(assetsTree.first { it.path == "svg" }.url)
        return svgTree.map { it.path.removeSuffix(".svg") }
    }


}
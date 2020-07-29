package io.sebi.twemojiamazing

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

suspend fun <T> List<T>.asyncFilter(transform: suspend (T) -> Boolean): List<T> {
    return map { it to GlobalScope.async { transform(it) } }
            .filter { (_, transformResult) ->
                transformResult.await()
            }
            .map {
                it.first
            }
}
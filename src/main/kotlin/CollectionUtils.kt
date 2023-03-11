package io.sebi.twemojiamazing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

suspend fun <T> List<T>.asyncFilter(scope: CoroutineScope, transform: suspend (T) -> Boolean): List<T> {
    return map { it to scope.async { transform(it) } }
        .filter { (_, transformResult) ->
            transformResult.await()
        }
        .map {
            it.first
        }
}
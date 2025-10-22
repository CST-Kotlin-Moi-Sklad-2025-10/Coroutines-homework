package ru.otus.homework

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.currentCoroutineContext

/**
 * Logs the coroutine
 */
suspend fun log(block: () -> String) {
    with(currentCoroutineContext()) {
        println("Coroutine: ${get(CoroutineName)}, thread: ${Thread.currentThread().name}, ${block()}")
    }
}

fun logThread(block: () -> String) {
    println("Thread: ${Thread.currentThread().name}, ${block()}")
}
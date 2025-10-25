package ru.otus.homework.scope

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.otus.homework.log
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.time.Duration.Companion.seconds

private const val numOfJobs = 10

fun main(): Unit = runBlocking {
    val numOfJobs = 5
    println("Jobs: requested($numOfJobs), complete(${runJobs(numOfJobs)})")
    println("Jobs: requested(3), complete(${runJobs(3)})")
    println("Jobs: requested(1), complete(${runJobs(1)})")
}

/**
 * Runs some jobs concurrently
 */
suspend fun runJobs(numOfJobs: Int, random: Random = Random): Int = coroutineScope {
    val jobs = List(numOfJobs) {
        async {
            try {
                doSomeJob(
                    duration = 1.seconds,
                    fails = random.nextBoolean()
                )
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    val results = jobs.awaitAll()
    return@coroutineScope results.count { it }
}
suspend fun doSomeJob(duration: Duration, fails: Boolean): String {
    log { "Starting job..." }
    delay(duration)
    if (fails) {
        log { "Job failed!" }
        throw RuntimeException("Job failed!")
    } else {
        log { "Job finished successfully" }
        return "Complete in: $duration"
    }
}

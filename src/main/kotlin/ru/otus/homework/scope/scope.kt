package ru.otus.homework.scope

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.otus.homework.log
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val numOfJobs = 10

fun main(): Unit = runBlocking {
    println("Jobs: requested($numOfJobs), complete(${runJobs(numOfJobs)})")
}

/**
 * Runs some jobs concurrently
 */
suspend fun runJobs(count: Int, random: Random = Random): Int {
    // 1. Launch some jobs concurrently
    val jobs = coroutineScope {
        (1..count).map { index ->
            async (CoroutineName("Job $index")) { // true for successful job, false otherwise
                try {
                    doSomeJob(
                        duration = random.nextInt(1, 5).seconds,
                        fails = random.nextBoolean()
                    )
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }
    }

    // 2. Await all jobs and count those that completed successfully
    return jobs.count { it.await() }
}

/**
 * Does some job with some duration and possibility of failure
 */
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

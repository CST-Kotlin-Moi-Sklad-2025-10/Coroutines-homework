package ru.otus.homework.scope

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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
    // 1. Launch some jobs concurrently using async to track success/failure
    val jobs = coroutineScope {
        (1..count).map { index ->
            async(CoroutineName("Job $index")) {
                try {
                    doSomeJob(
                        duration = random.nextInt(1, 5).seconds,
                        fails = random.nextBoolean()
                    )
                    true // Job succeeded
                } catch (e: Exception) {
                    // Catch exception to prevent cancellation of other jobs
                    false // Job failed
                }
            }
        }
    }

    // 2. Return number of completed jobs (count successful ones)
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

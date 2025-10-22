package ru.otus.homework.speedtest

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ru.otus.homework.log
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Minimum delay
 */
private const val minDelay = 100L

/**
 * Maximum delay
 */
private const val maxDelay = 1000L

/**
 * Will fail or not
 */
private fun Random.willFail(): Boolean = nextInt(1, 20) <= 5

class SpeedtestService(val random: Random = Random) {
    suspend fun measureRequestTime(): Result<Long> {
        val delay = random.nextLong(minDelay, maxDelay)
        var progress = 0L
        while (currentCoroutineContext().isActive && progress < delay) {
            if (random.willFail()) {
                log { "emulateBlockingNetworkRequest: Network request failed" }
                return Result.failure(Exception("Network request failed"))
            }
            log { "emulateBlockingNetworkRequest: progress = $progress, delay = $delay" }
            delay(100.milliseconds)
            progress += 100
        }
        log { "emulateBlockingNetworkRequest: Network request completed" }
        return Result.success(delay)
    }
}
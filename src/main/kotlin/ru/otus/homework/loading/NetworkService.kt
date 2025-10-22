package ru.otus.homework.loading

import ru.otus.homework.logThread
import java.io.IOException
import java.util.UUID
import kotlin.random.Random

private const val minDelay = 100L
private const val maxDelay = 1000L

class NetworkService(
    private val random: Random = Random,
    private val isOnMain: () -> Boolean = { Thread.currentThread().name.startsWith("main") }
) {
    fun loadProfile(id: UUID): UserProfile {
        check(isOnMain().not()) {
            "Should not be called from the main thread"
        }
        val toDelay = random.nextLong(minDelay, maxDelay)
        val willFail = random.nextBoolean()
        logThread { "Loading user profile. Delay: $toDelay, willFail: $willFail" }

        Thread.sleep(toDelay)
        if (willFail) {
            throw IOException("Network request failed")
        }
        return UserProfile(id, "User $id")
    }
}

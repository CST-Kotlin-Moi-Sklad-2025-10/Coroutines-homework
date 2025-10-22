package ru.otus.homework.speedtest

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SpeedtestTest {

    companion object {
        private const val NETWORK_DELAY = 1000L
        private const val NUM_OF_REQUESTS = 10
    }

    private lateinit var random: Random
    private lateinit var service: SpeedtestService

    @BeforeEach
    fun init() {
        random = mockk {
            // Network delay
            every { nextLong(any(), any()) } returns NETWORK_DELAY
        }
        service = SpeedtestService(random)
    }

    @Test
    fun calculatesAverageSpeedForAllSucceeded() = test {
        // All success
        every { random.nextInt(1, 20) } returns 10

        val speed = measureNetworkSpeed(NUM_OF_REQUESTS, service)

        assertEquals(NETWORK_DELAY.toDouble(), speed, 10.0)
    }

    @Test
    fun returnsNaNIfAllFailed() = test {
        // All fail
        every { random.nextInt(1, 20) } returns 1

        val speed = measureNetworkSpeed(NUM_OF_REQUESTS, service)

        assertEquals(Double.NaN, speed)
    }

    @Test
    fun runsConcurrently() = runTest(StandardTestDispatcher()) {
        // All success
        every { random.nextInt(1, 20) } returns 10

        val measureJob = backgroundScope.async {
            measureNetworkSpeed(NUM_OF_REQUESTS, service)
        }

        advanceTimeBy(NETWORK_DELAY * 3)

        assertTrue { measureJob.isCompleted }
    }

    private fun test(block: suspend TestScope.() -> Unit) = runTest(
        UnconfinedTestDispatcher(),
        testBody = block
    )
}
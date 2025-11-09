package ru.otus.homework.scope

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ScopeTest {
    private lateinit var random: Random

    @BeforeEach
    fun init() {
        random = mockk {
            every { nextInt(any(), any()) } returns 1
        }
    }

    @Test
    fun completesWhenJobsComplete() = test {
        val numOfJobs = 2
        every { random.nextBoolean() } returns false
        launch(CoroutineExceptionHandler { _, _ ->  }) {
            assertEquals(numOfJobs, runJobs(numOfJobs, random))
        }
    }

    @Test
    fun completesWhenJobsFails() = test {
        val numOfJobs = 2
        every { random.nextBoolean() } returnsMany listOf(false, true)
        launch(CoroutineExceptionHandler { _, _ ->  }) {
            assertEquals(1, runJobs(numOfJobs, random))
        }
    }

    @Test
    fun countsSuccessfullyCompletedJobsWhenSomeFail() = test {
        val numOfJobs = 20
        // First 5 calls to random.nextBoolean() return false (success),
        // remaining calls return true (failure)
        every { random.nextBoolean() } returnsMany (
                List(5) { false } + true
        )
        launch(CoroutineExceptionHandler { _, _ -> }) {
            assertEquals(5, runJobs(numOfJobs, random))
        }
    }

    private fun test(block: suspend TestScope.() -> Unit) = runTest(
        UnconfinedTestDispatcher(),
        testBody = block
    )
}
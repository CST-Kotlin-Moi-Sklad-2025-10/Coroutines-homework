package ru.otus.homework.loading

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import java.io.IOException
import java.util.UUID
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class LoadingTest {

    private lateinit var random: Random
    private val userId = UUID.randomUUID()

    @BeforeEach
    fun init() {
        random = mockk {
            every { nextLong(any(), any()) } returns 100L
        }
    }

    @Test
    fun loadsDataWhenNetworkCompletes() = test {
        every { random.nextBoolean() } returns false
        val currentThreadName = Thread.currentThread().name
        val service = NetworkService(random) {
            // Check we are using background thread
            currentThreadName == Thread.currentThread().name
        }

        val updates = loadProfile(userId, service).toList()

        assertEquals(2, updates.size)
        assertIs<LceState.Loading>(updates[0])
        assertIs<LceState.Content<*>>(updates[1])
    }

    @Test
    fun loadsDataWhenNetworkFails() = test {
        every { random.nextBoolean() } returns true
        val currentThreadName = Thread.currentThread().name
        val service = NetworkService(random) {
            // Check we are using background thread
            currentThreadName == Thread.currentThread().name
        }

        val updates = loadProfile(userId, service).toList()

        assertEquals(2, updates.size)
        assertIs<LceState.Loading>(updates[0])

        val error = assertIs<LceState.Error>(updates[1])
        assertIs<IOException>(error.error)
    }

    private fun test(block: suspend TestScope.() -> Unit) = runTest(
        UnconfinedTestDispatcher(),
        testBody = block
    )
}
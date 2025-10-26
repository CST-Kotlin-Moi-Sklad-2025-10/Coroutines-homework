package ru.otus.homework.speedtest

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

private const val numOfRequests = 20

fun main(): Unit = runBlocking {
    val service = SpeedtestService()
    val networkSpeed = measureNetworkSpeed(numOfRequests, service)
    println("Average network speed: ${String.format("%.2f", networkSpeed)} ms")
}

suspend fun measureNetworkSpeed(numOfRequests: Int, service: SpeedtestService): Double = coroutineScope {
    // 1. Launch numOfRequests async jobs concurrently
    val deferredResults = (1..numOfRequests).map {
        async {
            service.measureRequestTime()
        }
    }

    // 2. Wait for all results
    val results = deferredResults.awaitAll()

    // 3. Filter successful results and extract values
    val successfulResults = results.mapNotNull { result ->
        result.getOrNull()
    }

    // 4. Calculate average or return NaN if all failed
    if (successfulResults.isEmpty()) {
        Double.NaN
    } else {
        successfulResults.average()
    }
}


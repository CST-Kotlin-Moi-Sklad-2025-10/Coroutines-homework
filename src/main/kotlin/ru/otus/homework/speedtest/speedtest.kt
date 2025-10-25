package ru.otus.homework.speedtest

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

private const val numOfRequests = 20

fun main(): Unit = runBlocking {
    val service = SpeedtestService()
    val networkSpeed = measureNetworkSpeed(numOfRequests, service)
    println("Average network speed: ${String.format("%.2f", networkSpeed)} ms")
}

suspend fun measureNetworkSpeed(numOfRequests: Int, service: SpeedtestService): Double = coroutineScope {
    val deferredResults = (1..numOfRequests).map {
        async {
                service.measureRequestTime()
        }
    }

    val successfulResults = deferredResults.mapNotNull { deferred ->
        deferred.await().getOrNull()
    }

    if (successfulResults.isEmpty()) {
        Double.NaN
    } else {
        successfulResults.average()
    }
}


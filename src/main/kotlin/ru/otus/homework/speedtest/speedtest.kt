package ru.otus.homework.speedtest

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


private const val numOfRequests = 20

fun main(): Unit = runBlocking {
    val service = SpeedtestService()
    val networkSpeed = measureNetworkSpeed(numOfRequests, service)
    println("Average network speed: ${String.format("%.2f", networkSpeed)} ms")
}

suspend fun measureNetworkSpeed(numOfRequests: Int, service: SpeedtestService): Double = coroutineScope {
    val requests = List(numOfRequests) {
        async {
            val result = service.measureRequestTime()

            result.getOrNull()?.toDouble()
        }
    }

    val successfulResults = requests.awaitAll().filterNotNull()

    if (successfulResults.isEmpty()) {
        Double.NaN
    } else {
        successfulResults.average()
    }
}

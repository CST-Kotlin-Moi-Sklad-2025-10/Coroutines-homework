package ru.otus.homework.loading

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

fun main(): Unit = runBlocking {
    val userId = UUID.randomUUID()
    val service = NetworkService()

    println("Loading user profile: $userId")
    loadProfile(userId, service).collect { state ->
        println("Update: $state")
    }
    println("Done")
}

fun loadProfile(id: UUID, service: NetworkService): Flow<LceState<UserProfile>> = channelFlow {
    send(LceState.Loading)
    withContext(Dispatchers.IO) {
        launch {
            try {
                val result = service.loadProfile(id)
                send(LceState.Content(result))
            } catch (t: Throwable){
                send(LceState.Error(t))
            }
        }
    }
}






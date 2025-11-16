package ru.otus.homework.loading

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

fun loadProfile(id: UUID, service: NetworkService): Flow<LceState<UserProfile>> = flow {
    emit(LceState.Loading)
    val resultState = withContext(Dispatchers.IO) {
        try {
            val result = service.loadProfile(id)
            LceState.Content(result)
        } catch (t: Throwable) {
            LceState.Error((t))
        }
    }
    emit(resultState)
}






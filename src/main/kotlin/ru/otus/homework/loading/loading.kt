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

    try {
        val profile = withContext(Dispatchers.IO) {
            service.loadProfile(id)
        }
        emit(LceState.Content(profile))
    } catch (e: Exception) {
        emit(LceState.Error(e))
    }
}






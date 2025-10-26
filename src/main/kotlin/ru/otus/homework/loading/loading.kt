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
    // 1. Emit Loading state
    emit(LceState.Loading)

    try {
        // 2. Load profile on background thread
        val profile = withContext(Dispatchers.IO) {
            service.loadProfile(id)
        }
        // 3. Emit Content state with loaded data
        emit(LceState.Content(profile))
    } catch (e: Exception) {
        // 4. Emit Error state if exception occurs
        emit(LceState.Error(e))
    }
}






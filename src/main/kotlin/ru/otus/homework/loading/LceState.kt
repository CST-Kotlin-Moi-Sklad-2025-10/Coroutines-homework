package ru.otus.homework.loading

/**
 * Loading-content-error state of data loading
 */
sealed class LceState<out DATA: Any> {
    data object Loading : LceState<Nothing>()
    data class Content<out DATA: Any>(val data: DATA) : LceState<DATA>()
    data class Error(val error: Throwable) : LceState<Nothing>()
}
package com.project.lolstudio.core.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SuspendLazy<T : Any>(private val initializer: suspend () -> T) {
    private val mutex = Mutex()
    private var value: T? = null

    suspend fun get(): T = mutex.withLock {
        value ?: initializer().also { value = it }
    }
}

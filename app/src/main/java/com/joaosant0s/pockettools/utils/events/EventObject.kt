package com.joaosant0s.pockettools.utils.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventObject {
    private val _events = MutableSharedFlow<Pair<String, Any?>>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: String, data: Any? = null) {
        _events.emit(event to data)
    }
}

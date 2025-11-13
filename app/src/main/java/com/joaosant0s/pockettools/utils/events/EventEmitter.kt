package com.joaosant0s.pockettools.utils.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface EventEmitter {
    fun emit(eventName: String) {
        CoroutineScope(Dispatchers.Default).launch {
            EventObject.emit(eventName)
        }
    }
}
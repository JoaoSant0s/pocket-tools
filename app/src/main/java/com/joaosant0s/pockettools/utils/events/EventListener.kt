package com.joaosant0s.pockettools.utils.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface EventListener {
    fun addListener() {
        val eventJob = CoroutineScope(Dispatchers.Main).launch {
            EventObject.events.collect { (event) -> onEventTriggered(event) }
        }
        EventManager.addEventListener(this, eventJob)
    }

    fun removeListener() {
        EventManager.removeEventListener(this)
    }

    fun onEventTriggered(eventName: String)
}
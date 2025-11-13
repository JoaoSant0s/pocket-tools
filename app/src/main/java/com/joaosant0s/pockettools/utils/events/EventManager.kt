package com.joaosant0s.pockettools.utils.events

import kotlinx.coroutines.Job

class EventManager {
    companion object {

        private val eventJobs: MutableMap<EventListener, Job> = mutableMapOf()

        internal fun addEventListener(eventListener : EventListener, job : Job)
        {
            removeEventListener(eventListener)
            eventJobs[eventListener] = job
        }

        internal fun removeEventListener(eventListener : EventListener)
        {
            eventJobs[eventListener]?.cancel()
            eventJobs.remove(eventListener)
        }
    }
}
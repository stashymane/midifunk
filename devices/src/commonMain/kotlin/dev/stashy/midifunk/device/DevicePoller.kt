package dev.stashy.midifunk.device

import kotlinx.coroutines.*

internal object DevicePoller {
    private val awaitingIds: MutableMap<String, MutableList<CompletableDeferred<MidiDevice>>> = mutableMapOf()
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private var job: Job? = null

    private fun launchPoller() {
        if (job != null) return

        job = scope.launch {
            while (awaitingIds.isNotEmpty()) {
                checkDevices()
                delay(1000L)
            }
        }
    }

    private fun checkDevices() {
        val devices = MidiDevice.list().associateBy { it.id }
        devices.forEach { (id, device) ->
            awaitingIds[id]?.forEach {
                it.complete(device)
            }
            awaitingIds.remove(id)
        }
    }

    fun awaitDevice(id: String): CompletableDeferred<MidiDevice> {
        val deferred = CompletableDeferred<MidiDevice>()
        awaitingIds.getOrPut(id) { mutableListOf() } += deferred
        launchPoller()
        return deferred
    }
}

package com.example.projetosaveit.api.network

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

object WebSocketClient {

    private const val TAG = "WebSocketClient"
    private const val SOCKET_URL = "wss://apimongosaveit.onrender.com/ws"

    private val stompClient: StompClient by lazy {
        Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL)
    }

    private var topicSubscription: Disposable? = null
    private var connected = false
    private var manuallyDisconnected = false
    private var reconnectJob: Job? = null

    fun connect(onMessageReceived: (String) -> Unit) {
        if (connected && topicSubscription != null && !topicSubscription!!.isDisposed) {
            Log.d(TAG, "Já conectado ao WebSocket")
            return
        }

        manuallyDisconnected = false
        Log.d(TAG, "Conectando ao WebSocket...")

        stompClient.connect()

        stompClient.lifecycle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.i(TAG, "Conectado ao WebSocket")
                        connected = true
                        reconnectJob?.cancel()
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.w(TAG, "Conexão WebSocket fechada")
                        connected = false
                        if (!manuallyDisconnected) scheduleReconnect(onMessageReceived)
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(TAG, "Erro no WebSocket", event.exception)
                        connected = false
                        if (!manuallyDisconnected) scheduleReconnect(onMessageReceived)
                    }
                    else -> {}
                }
            }

        topicSubscription = stompClient.topic("/topic/messages")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ message ->
                Log.d(TAG, "Mensagem recebida: ${message.payload}")
                onMessageReceived(message.payload)
            }, { error ->
                Log.e(TAG, "Erro ao receber mensagem", error)
            })
    }

    fun sendMessage(jsonBody: String) {
        if (!connected) {
            Log.w(TAG, "Sem conexão — tentando reconectar antes de enviar...")
            reconnectJob?.cancel()
            scheduleReconnect { sendMessage(jsonBody) }
            return
        }

        stompClient.send("/app/enviarMensagem", jsonBody)
            .subscribe({}, { e -> Log.e(TAG, "Erro ao enviar mensagem", e) })
    }

    fun disconnect() {
        manuallyDisconnected = true
        reconnectJob?.cancel()
        topicSubscription?.dispose()
        stompClient.disconnect()
        connected = false
        Log.i(TAG, "WebSocket desconectado manualmente")
    }

    private fun scheduleReconnect(onMessageReceived: (String) -> Unit) {
        if (reconnectJob?.isActive == true) return
        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            delay(5000)
            if (!manuallyDisconnected && !connected) {
                Log.d(TAG, "Tentando reconectar ao WebSocket...")
                connect(onMessageReceived)
            }
        }
    }

    fun isConnected(): Boolean = connected
}
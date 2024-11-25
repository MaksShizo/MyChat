package com.lenincompany.mychat.ui.chat

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ChatWebSocket(
    private val serverUrl: String,
    private val onMessageReceived: (String) -> Unit,
    private val onOpen: () -> Unit,
    private val onError: (String) -> Unit
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect(chatId: Int) {
        val request = Request.Builder()
            .url("$serverUrl/$chatId") // Передача chatId
            .build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@ChatWebSocket.webSocket = webSocket
                onOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError(t.message ?: "Unknown error")
            }
        }

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun sendMessage(message: String) {
        webSocket?.let {
            if (it.send(message)) {
                println("Message sent: $message")
            } else {
                println("Failed to send message")
            }
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected")
        webSocket = null
    }
}
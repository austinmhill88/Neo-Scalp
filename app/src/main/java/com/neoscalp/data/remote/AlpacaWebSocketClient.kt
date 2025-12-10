package com.neoscalp.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.neoscalp.data.model.BarUpdate
import com.neoscalp.data.model.TradeUpdate
import com.neoscalp.data.model.QuoteUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit

class AlpacaWebSocketClient(
    private val apiKey: String,
    private val apiSecret: String,
    private val isPaper: Boolean = true
) {
    private val TAG = "AlpacaWebSocket"
    private var webSocket: WebSocket? = null
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _tradeUpdates = MutableSharedFlow<TradeUpdate>(replay = 0)
    val tradeUpdates: SharedFlow<TradeUpdate> = _tradeUpdates

    private val _quoteUpdates = MutableSharedFlow<QuoteUpdate>(replay = 0)
    val quoteUpdates: SharedFlow<QuoteUpdate> = _quoteUpdates

    private val _barUpdates = MutableSharedFlow<BarUpdate>(replay = 0)
    val barUpdates: SharedFlow<BarUpdate> = _barUpdates

    private val _connectionStatus = MutableSharedFlow<ConnectionStatus>(replay = 1)
    val connectionStatus: SharedFlow<ConnectionStatus> = _connectionStatus

    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val wsUrl = "wss://stream.data.alpaca.markets/v2/sip"

    fun connect() {
        if (webSocket != null) {
            Log.d(TAG, "WebSocket already connected")
            return
        }

        val request = Request.Builder()
            .url(wsUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                scope.launch {
                    _connectionStatus.emit(ConnectionStatus.CONNECTED)
                }
                authenticate()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error: ${t.message}", t)
                scope.launch {
                    _connectionStatus.emit(ConnectionStatus.ERROR)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $reason")
                scope.launch {
                    _connectionStatus.emit(ConnectionStatus.DISCONNECTED)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $reason")
                scope.launch {
                    _connectionStatus.emit(ConnectionStatus.DISCONNECTED)
                }
            }
        })
    }

    private fun authenticate() {
        val authMessage = JsonObject().apply {
            addProperty("action", "auth")
            addProperty("key", apiKey)
            addProperty("secret", apiSecret)
        }
        webSocket?.send(gson.toJson(authMessage))
    }

    fun subscribe(symbols: List<String>) {
        val subscribeMessage = JsonObject().apply {
            addProperty("action", "subscribe")
            add("trades", gson.toJsonTree(symbols))
            add("quotes", gson.toJsonTree(symbols))
            add("bars", gson.toJsonTree(symbols))
        }
        webSocket?.send(gson.toJson(subscribeMessage))
        Log.d(TAG, "Subscribed to: $symbols")
    }

    fun unsubscribe(symbols: List<String>) {
        val unsubscribeMessage = JsonObject().apply {
            addProperty("action", "unsubscribe")
            add("trades", gson.toJsonTree(symbols))
            add("quotes", gson.toJsonTree(symbols))
            add("bars", gson.toJsonTree(symbols))
        }
        webSocket?.send(gson.toJson(unsubscribeMessage))
    }

    private fun handleMessage(text: String) {
        try {
            val messages = gson.fromJson(text, Array<JsonObject>::class.java)
            
            messages.forEach { message ->
                when (message.get("T")?.asString) {
                    "t" -> handleTradeUpdate(message)
                    "q" -> handleQuoteUpdate(message)
                    "b" -> handleBarUpdate(message)
                    "success" -> Log.d(TAG, "Message: ${message.get("msg")?.asString}")
                    "error" -> Log.e(TAG, "Error: ${message.get("msg")?.asString}")
                    "subscription" -> Log.d(TAG, "Subscription confirmed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}", e)
        }
    }

    private fun handleTradeUpdate(json: JsonObject) {
        try {
            val trade = TradeUpdate(
                symbol = json.get("S").asString,
                price = json.get("p").asDouble,
                size = json.get("s").asLong,
                timestamp = json.get("t").asLong
            )
            scope.launch {
                _tradeUpdates.emit(trade)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing trade: ${e.message}")
        }
    }

    private fun handleQuoteUpdate(json: JsonObject) {
        try {
            val quote = QuoteUpdate(
                symbol = json.get("S").asString,
                bidPrice = json.get("bp").asDouble,
                bidSize = json.get("bs").asLong,
                askPrice = json.get("ap").asDouble,
                askSize = json.get("as").asLong,
                timestamp = json.get("t").asLong
            )
            scope.launch {
                _quoteUpdates.emit(quote)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing quote: ${e.message}")
        }
    }

    private fun handleBarUpdate(json: JsonObject) {
        try {
            val bar = BarUpdate(
                symbol = json.get("S").asString,
                open = json.get("o").asDouble,
                high = json.get("h").asDouble,
                low = json.get("l").asDouble,
                close = json.get("c").asDouble,
                volume = json.get("v").asLong,
                timestamp = json.get("t").asLong
            )
            scope.launch {
                _barUpdates.emit(bar)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing bar: ${e.message}")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closing")
        webSocket = null
    }

    enum class ConnectionStatus {
        CONNECTED,
        DISCONNECTED,
        ERROR,
        RECONNECTING
    }
}

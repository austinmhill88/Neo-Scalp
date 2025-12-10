package com.neoscalp.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.neoscalp.R
import com.neoscalp.data.remote.AlpacaWebSocketClient
import com.neoscalp.domain.model.BotStatus
import com.neoscalp.domain.model.CandleData
import com.neoscalp.domain.model.SignalType
import com.neoscalp.domain.usecase.ScalpingBotEngine
import com.neoscalp.data.repository.TradingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TradingBotService : Service() {

    @Inject
    lateinit var botEngine: ScalpingBotEngine

    @Inject
    lateinit var repository: TradingRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val TAG = "TradingBotService"
    private val CHANNEL_ID = "trading_bot_channel"
    private val NOTIFICATION_ID = 1

    private var currentPrice = 0.0
    private var currentSymbol = "JNJ"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        
        val notification = createNotification("Bot is starting...", "Initializing")
        startForeground(NOTIFICATION_ID, notification)

        startBot()

        return START_STICKY
    }

    private fun startBot() {
        serviceScope.launch {
            botEngine.start()
            
            val wsClient = repository.getWebSocketClient()
            if (wsClient != null) {
                // Connect to WebSocket
                wsClient.connect()
                
                // Wait for connection
                delay(2000)
                
                // Subscribe to symbol
                wsClient.subscribe(listOf(currentSymbol))
                
                // Collect bar updates
                launch {
                    wsClient.barUpdates.collectLatest { bar ->
                        handleBarUpdate(bar.close, bar.open, bar.high, bar.low, bar.volume, bar.timestamp)
                    }
                }
                
                // Collect trade updates for price
                launch {
                    wsClient.tradeUpdates.collectLatest { trade ->
                        currentPrice = trade.price
                        analyzeAndUpdate()
                    }
                }

                // Monitor bot status
                launch {
                    botEngine.botStatus.collect { status ->
                        when (status) {
                            BotStatus.RUNNING -> updateNotification("Bot running", "Monitoring $currentSymbol")
                            BotStatus.STOPPED -> stopSelf()
                            BotStatus.PAUSED -> updateNotification("Bot paused", "Paused")
                            BotStatus.ERROR -> updateNotification("Bot error", "Check logs")
                        }
                    }
                }

                // Monitor signals
                launch {
                    botEngine.currentSignal.collect { signal ->
                        signal?.let {
                            when (it.type) {
                                SignalType.ENTRY_LONG -> {
                                    showSignalNotification("Entry Signal", "Long entry at ${it.price}")
                                }
                                SignalType.ENTRY_SHORT -> {
                                    showSignalNotification("Entry Signal", "Short entry at ${it.price}")
                                }
                                SignalType.EXIT_LONG, SignalType.EXIT_SHORT -> {
                                    showSignalNotification("Exit Signal", "Exit at ${it.price}")
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleBarUpdate(close: Double, open: Double, high: Double, low: Double, volume: Long, timestamp: Long) {
        val candle = CandleData(timestamp, open, high, low, close, volume)
        botEngine.addCandle(candle)
        currentPrice = close
        analyzeAndUpdate()
    }

    private fun analyzeAndUpdate() {
        serviceScope.launch {
            val signal = botEngine.analyzeMarket(currentPrice)
            val mode = botEngine.detectTradingMode()
            
            updateNotification(
                "$currentSymbol: $${"%.2f".format(currentPrice)}",
                "Mode: $mode | Signal: ${signal.type}"
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Trading Bot",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Trading bot status notifications"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showSignalNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        botEngine.stop()
        repository.getWebSocketClient()?.disconnect()
        serviceScope.cancel()
    }
}

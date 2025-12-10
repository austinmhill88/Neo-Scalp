package com.neoscalp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Trading Bot Configuration
data class BotConfig(
    val symbol: String = "JNJ",
    val ema9Period: Double = 9.0,
    val ema21Period: Double = 21.0,
    val rsiPeriod: Int = 14,
    val rsiOversold: Double = 30.0,
    val rsiOverbought: Double = 70.0,
    val macdFast: Int = 12,
    val macdSlow: Int = 26,
    val macdSignal: Int = 9,
    val bollingerPeriod: Int = 20,
    val bollingerStdDev: Double = 2.0,
    val stochasticK: Int = 14,
    val stochasticD: Int = 3,
    val stochasticSlowing: Int = 3,
    val targetPercent: Double = 0.5,
    val stopPercent: Double = 0.3,
    val maxRiskPercent: Double = 1.0,
    val dailyLossLimit: Double = 3.0,
    val useTrailingStop: Boolean = true,
    val semiAutoMode: Boolean = false,
    val isPaperMode: Boolean = true
)

// Trading Mode
enum class TradingMode {
    LONG,
    SHORT,
    NEUTRAL
}

// Bot Status
enum class BotStatus {
    STOPPED,
    RUNNING,
    PAUSED,
    ERROR
}

// Trade Signal
data class TradeSignal(
    val type: SignalType,
    val symbol: String,
    val price: Double,
    val confidence: Double,
    val indicators: Map<String, Double>,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SignalType {
    ENTRY_LONG,
    ENTRY_SHORT,
    EXIT_LONG,
    EXIT_SHORT,
    HOLD,
    NO_SIGNAL
}

// Technical Indicators
data class Indicators(
    val ema9: Double? = null,
    val ema21: Double? = null,
    val vwap: Double? = null,
    val rsi: Double? = null,
    val macd: Double? = null,
    val macdSignal: Double? = null,
    val macdHistogram: Double? = null,
    val bollingerUpper: Double? = null,
    val bollingerMiddle: Double? = null,
    val bollingerLower: Double? = null,
    val stochasticK: Double? = null,
    val stochasticD: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// Trade History Entry
@Entity(tableName = "trades")
data class TradeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val symbol: String,
    val side: String, // "buy" or "sell"
    val qty: Double,
    val entryPrice: Double,
    val exitPrice: Double? = null,
    val entryTime: Long,
    val exitTime: Long? = null,
    val profitLoss: Double? = null,
    val profitLossPercent: Double? = null,
    val indicators: String, // JSON string of indicator values
    val status: String, // "open", "closed", "cancelled"
    val notes: String? = null
)

// Market Data Snapshot
data class MarketSnapshot(
    val symbol: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val bars: List<CandleData>,
    val indicators: Indicators,
    val timestamp: Long = System.currentTimeMillis()
)

// Candle Data for Charts
data class CandleData(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

// Account Status
data class AccountStatus(
    val equity: Double,
    val cash: Double,
    val buyingPower: Double,
    val portfolioValue: Double,
    val dayTradeCount: Int,
    val isPatternDayTrader: Boolean,
    val todayPnL: Double = 0.0,
    val totalPnL: Double = 0.0
)

// Position Info
data class PositionInfo(
    val symbol: String,
    val qty: Double,
    val side: String,
    val avgEntryPrice: Double,
    val currentPrice: Double,
    val unrealizedPnL: Double,
    val unrealizedPnLPercent: Double,
    val marketValue: Double
)

// Backtest Result
data class BacktestResult(
    val symbol: String,
    val startDate: Long,
    val endDate: Long,
    val totalTrades: Int,
    val winningTrades: Int,
    val losingTrades: Int,
    val winRate: Double,
    val totalPnL: Double,
    val averageWin: Double,
    val averageLoss: Double,
    val maxDrawdown: Double,
    val sharpeRatio: Double,
    val trades: List<TradeEntry>
)

// Watchlist Symbol
data class WatchlistSymbol(
    val symbol: String,
    val name: String,
    val isActive: Boolean = false
)

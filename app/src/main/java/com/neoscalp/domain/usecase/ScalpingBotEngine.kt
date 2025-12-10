package com.neoscalp.domain.usecase

import android.util.Log
import com.neoscalp.data.model.OrderRequest
import com.neoscalp.data.repository.TradingRepository
import com.neoscalp.domain.model.*
import com.neoscalp.utils.IndicatorCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class ScalpingBotEngine @Inject constructor(
    private val repository: TradingRepository,
    private val indicatorCalculator: IndicatorCalculator
) {
    private val TAG = "ScalpingBotEngine"

    private val _botStatus = MutableStateFlow(BotStatus.STOPPED)
    val botStatus: StateFlow<BotStatus> = _botStatus

    private val _currentMode = MutableStateFlow(TradingMode.NEUTRAL)
    val currentMode: StateFlow<TradingMode> = _currentMode

    private val _currentSignal = MutableStateFlow<TradeSignal?>(null)
    val currentSignal: StateFlow<TradeSignal?> = _currentSignal

    private var config: BotConfig = BotConfig()
    private var candles = mutableListOf<CandleData>()
    private var currentPosition: PositionInfo? = null

    fun updateConfig(newConfig: BotConfig) {
        config = newConfig
        Log.d(TAG, "Config updated: $config")
    }

    fun start() {
        _botStatus.value = BotStatus.RUNNING
        Log.d(TAG, "Bot started")
    }

    fun stop() {
        _botStatus.value = BotStatus.STOPPED
        Log.d(TAG, "Bot stopped")
    }

    fun pause() {
        _botStatus.value = BotStatus.PAUSED
        Log.d(TAG, "Bot paused")
    }

    fun updateCandles(newCandles: List<CandleData>) {
        candles.clear()
        candles.addAll(newCandles)
    }

    fun addCandle(candle: CandleData) {
        candles.add(candle)
        // Keep only recent candles (e.g., last 200)
        if (candles.size > 200) {
            candles.removeAt(0)
        }
    }

    /**
     * Determine trading mode based on market conditions
     */
    fun detectTradingMode(): TradingMode {
        if (candles.size < config.ema21Period.toInt()) {
            return TradingMode.NEUTRAL
        }

        val closePrices = candles.map { it.close }
        val ema9 = IndicatorCalculator.calculateEMA(closePrices, config.ema9Period.toInt())
        val ema21 = IndicatorCalculator.calculateEMA(closePrices, config.ema21Period.toInt())
        val vwapSlope = IndicatorCalculator.calculateVWAPSlope(candles, 10)

        if (ema9 == null || ema21 == null) {
            return TradingMode.NEUTRAL
        }

        val mode = when {
            ema9 > ema21 && (vwapSlope ?: 0.0) > 0 -> TradingMode.LONG
            ema9 < ema21 && (vwapSlope ?: 0.0) < 0 -> TradingMode.SHORT
            else -> TradingMode.NEUTRAL
        }

        _currentMode.value = mode
        return mode
    }

    /**
     * Analyze market and generate trading signals
     */
    fun analyzeMarket(currentPrice: Double): TradeSignal {
        if (candles.isEmpty()) {
            return TradeSignal(
                type = SignalType.NO_SIGNAL,
                symbol = config.symbol,
                price = currentPrice,
                confidence = 0.0,
                indicators = emptyMap()
            )
        }

        val indicators = calculateIndicators()
        val mode = detectTradingMode()

        // Check for entry signals
        val entrySignal = detectEntrySignal(currentPrice, indicators, mode)
        if (entrySignal != null) {
            _currentSignal.value = entrySignal
            return entrySignal
        }

        // Check for exit signals if we have a position
        if (currentPosition != null) {
            val exitSignal = detectExitSignal(currentPrice, indicators, mode)
            if (exitSignal != null) {
                _currentSignal.value = exitSignal
                return exitSignal
            }
        }

        val holdSignal = TradeSignal(
            type = SignalType.HOLD,
            symbol = config.symbol,
            price = currentPrice,
            confidence = 0.0,
            indicators = convertIndicatorsToMap(indicators)
        )
        _currentSignal.value = holdSignal
        return holdSignal
    }

    private fun calculateIndicators(): Indicators {
        val closePrices = candles.map { it.close }
        
        val ema9 = IndicatorCalculator.calculateEMA(closePrices, config.ema9Period.toInt())
        val ema21 = IndicatorCalculator.calculateEMA(closePrices, config.ema21Period.toInt())
        val vwap = IndicatorCalculator.calculateVWAP(candles)
        val rsi = IndicatorCalculator.calculateRSI(closePrices, config.rsiPeriod)
        val macd = IndicatorCalculator.calculateMACD(
            closePrices,
            config.macdFast,
            config.macdSlow,
            config.macdSignal
        )
        val bollinger = IndicatorCalculator.calculateBollingerBands(
            closePrices,
            config.bollingerPeriod,
            config.bollingerStdDev
        )
        val stochastic = IndicatorCalculator.calculateStochastic(
            candles,
            config.stochasticK,
            config.stochasticD,
            config.stochasticSlowing
        )

        return Indicators(
            ema9 = ema9,
            ema21 = ema21,
            vwap = vwap,
            rsi = rsi,
            macd = macd?.macd,
            macdSignal = macd?.signal,
            macdHistogram = macd?.histogram,
            bollingerUpper = bollinger?.upper,
            bollingerMiddle = bollinger?.middle,
            bollingerLower = bollinger?.lower,
            stochasticK = stochastic?.k,
            stochasticD = stochastic?.d
        )
    }

    private fun detectEntrySignal(
        currentPrice: Double,
        indicators: Indicators,
        mode: TradingMode
    ): TradeSignal? {
        val closePrices = candles.map { it.close }
        val crossover = IndicatorCalculator.detectEMACrossover(
            closePrices,
            config.ema9Period.toInt(),
            config.ema21Period.toInt()
        )

        // Long entry conditions
        if (mode == TradingMode.LONG) {
            var confidence = 0.0
            var signals = 0
            var total = 0

            // RSI oversold
            total++
            if (indicators.rsi != null && indicators.rsi < config.rsiOversold) {
                signals++
                confidence += 0.2
            }

            // Stochastic oversold
            total++
            if (indicators.stochasticK != null && indicators.stochasticK < 20.0) {
                signals++
                confidence += 0.15
            }

            // Price above VWAP
            total++
            if (indicators.vwap != null && currentPrice > indicators.vwap) {
                signals++
                confidence += 0.15
            }

            // MACD bullish crossover
            total++
            if (indicators.macdHistogram != null && indicators.macdHistogram > 0) {
                signals++
                confidence += 0.2
            }

            // Bollinger lower band touch
            total++
            if (indicators.bollingerLower != null && currentPrice <= indicators.bollingerLower) {
                signals++
                confidence += 0.15
            }

            // EMA crossover
            total++
            if (crossover == IndicatorCalculator.CrossoverType.BULLISH) {
                signals++
                confidence += 0.15
            }

            // Require at least 3 confluence signals
            if (signals >= 3) {
                return TradeSignal(
                    type = SignalType.ENTRY_LONG,
                    symbol = config.symbol,
                    price = currentPrice,
                    confidence = confidence,
                    indicators = convertIndicatorsToMap(indicators)
                )
            }
        }

        // Short entry conditions
        if (mode == TradingMode.SHORT) {
            var confidence = 0.0
            var signals = 0

            // RSI overbought
            if (indicators.rsi != null && indicators.rsi > config.rsiOverbought) {
                signals++
                confidence += 0.2
            }

            // Stochastic overbought
            if (indicators.stochasticK != null && indicators.stochasticK > 80.0) {
                signals++
                confidence += 0.15
            }

            // Price below VWAP
            if (indicators.vwap != null && currentPrice < indicators.vwap) {
                signals++
                confidence += 0.15
            }

            // MACD bearish crossover
            if (indicators.macdHistogram != null && indicators.macdHistogram < 0) {
                signals++
                confidence += 0.2
            }

            // Bollinger upper band touch
            if (indicators.bollingerUpper != null && currentPrice >= indicators.bollingerUpper) {
                signals++
                confidence += 0.15
            }

            // EMA crossover
            if (crossover == IndicatorCalculator.CrossoverType.BEARISH) {
                signals++
                confidence += 0.15
            }

            // Require at least 3 confluence signals
            if (signals >= 3) {
                return TradeSignal(
                    type = SignalType.ENTRY_SHORT,
                    symbol = config.symbol,
                    price = currentPrice,
                    confidence = confidence,
                    indicators = convertIndicatorsToMap(indicators)
                )
            }
        }

        return null
    }

    private fun detectExitSignal(
        currentPrice: Double,
        indicators: Indicators,
        mode: TradingMode
    ): TradeSignal? {
        val position = currentPosition ?: return null
        val entryPrice = position.avgEntryPrice
        val profitPercent = ((currentPrice - entryPrice) / entryPrice) * 100

        // Exit on target
        if (position.side == "long" && profitPercent >= config.targetPercent) {
            return TradeSignal(
                type = SignalType.EXIT_LONG,
                symbol = config.symbol,
                price = currentPrice,
                confidence = 1.0,
                indicators = convertIndicatorsToMap(indicators)
            )
        }

        if (position.side == "short" && profitPercent <= -config.targetPercent) {
            return TradeSignal(
                type = SignalType.EXIT_SHORT,
                symbol = config.symbol,
                price = currentPrice,
                confidence = 1.0,
                indicators = convertIndicatorsToMap(indicators)
            )
        }

        // Exit on stop loss
        if (position.side == "long" && profitPercent <= -config.stopPercent) {
            return TradeSignal(
                type = SignalType.EXIT_LONG,
                symbol = config.symbol,
                price = currentPrice,
                confidence = 1.0,
                indicators = convertIndicatorsToMap(indicators)
            )
        }

        if (position.side == "short" && profitPercent >= config.stopPercent) {
            return TradeSignal(
                type = SignalType.EXIT_SHORT,
                symbol = config.symbol,
                price = currentPrice,
                confidence = 1.0,
                indicators = convertIndicatorsToMap(indicators)
            )
        }

        // Exit on reversal signals
        if (position.side == "long" && indicators.macdHistogram != null && indicators.macdHistogram < 0) {
            if (indicators.rsi != null && indicators.rsi > config.rsiOverbought) {
                return TradeSignal(
                    type = SignalType.EXIT_LONG,
                    symbol = config.symbol,
                    price = currentPrice,
                    confidence = 0.7,
                    indicators = convertIndicatorsToMap(indicators)
                )
            }
        }

        if (position.side == "short" && indicators.macdHistogram != null && indicators.macdHistogram > 0) {
            if (indicators.rsi != null && indicators.rsi < config.rsiOversold) {
                return TradeSignal(
                    type = SignalType.EXIT_SHORT,
                    symbol = config.symbol,
                    price = currentPrice,
                    confidence = 0.7,
                    indicators = convertIndicatorsToMap(indicators)
                )
            }
        }

        return null
    }

    fun calculatePositionSize(equity: Double, price: Double): Int {
        val riskAmount = equity * (config.maxRiskPercent / 100.0)
        val qty = (riskAmount / (price * (config.stopPercent / 100.0))).toInt()
        return maxOf(1, qty)
    }

    fun updatePosition(position: PositionInfo?) {
        currentPosition = position
    }

    private fun convertIndicatorsToMap(indicators: Indicators): Map<String, Double> {
        return buildMap {
            indicators.ema9?.let { put("ema9", it) }
            indicators.ema21?.let { put("ema21", it) }
            indicators.vwap?.let { put("vwap", it) }
            indicators.rsi?.let { put("rsi", it) }
            indicators.macd?.let { put("macd", it) }
            indicators.macdSignal?.let { put("macdSignal", it) }
            indicators.macdHistogram?.let { put("macdHistogram", it) }
            indicators.bollingerUpper?.let { put("bollingerUpper", it) }
            indicators.bollingerMiddle?.let { put("bollingerMiddle", it) }
            indicators.bollingerLower?.let { put("bollingerLower", it) }
            indicators.stochasticK?.let { put("stochasticK", it) }
            indicators.stochasticD?.let { put("stochasticD", it) }
        }
    }
}

package com.neoscalp.utils

import com.neoscalp.domain.model.CandleData
import kotlin.math.pow
import kotlin.math.sqrt

object IndicatorCalculator {

    /**
     * Calculate Exponential Moving Average (EMA)
     */
    fun calculateEMA(prices: List<Double>, period: Int): Double? {
        if (prices.size < period) return null
        
        val multiplier = 2.0 / (period + 1)
        var ema = prices.take(period).average()
        
        for (i in period until prices.size) {
            ema = (prices[i] - ema) * multiplier + ema
        }
        
        return ema
    }

    /**
     * Calculate all EMAs for a series
     */
    fun calculateEMASeries(prices: List<Double>, period: Int): List<Double?> {
        if (prices.size < period) return List(prices.size) { null }
        
        val result = mutableListOf<Double?>()
        val multiplier = 2.0 / (period + 1)
        
        // First EMA is SMA
        repeat(period - 1) { result.add(null) }
        var ema = prices.take(period).average()
        result.add(ema)
        
        for (i in period until prices.size) {
            ema = (prices[i] - ema) * multiplier + ema
            result.add(ema)
        }
        
        return result
    }

    /**
     * Calculate Volume Weighted Average Price (VWAP)
     */
    fun calculateVWAP(candles: List<CandleData>): Double? {
        if (candles.isEmpty()) return null
        
        var sumPriceVolume = 0.0
        var sumVolume = 0L
        
        candles.forEach { candle ->
            val typical = (candle.high + candle.low + candle.close) / 3.0
            sumPriceVolume += typical * candle.volume
            sumVolume += candle.volume
        }
        
        return if (sumVolume > 0) sumPriceVolume / sumVolume else null
    }

    /**
     * Calculate RSI (Relative Strength Index)
     */
    fun calculateRSI(prices: List<Double>, period: Int = 14): Double? {
        if (prices.size <= period) return null
        
        val changes = mutableListOf<Double>()
        for (i in 1 until prices.size) {
            changes.add(prices[i] - prices[i - 1])
        }
        
        var avgGain = 0.0
        var avgLoss = 0.0
        
        // Initial averages
        for (i in 0 until period) {
            if (changes[i] > 0) avgGain += changes[i]
            else avgLoss -= changes[i]
        }
        avgGain /= period
        avgLoss /= period
        
        // Smooth with remaining values
        for (i in period until changes.size) {
            if (changes[i] > 0) {
                avgGain = (avgGain * (period - 1) + changes[i]) / period
                avgLoss = (avgLoss * (period - 1)) / period
            } else {
                avgGain = (avgGain * (period - 1)) / period
                avgLoss = (avgLoss * (period - 1) - changes[i]) / period
            }
        }
        
        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }

    /**
     * Calculate MACD (Moving Average Convergence Divergence)
     */
    data class MACDResult(val macd: Double, val signal: Double, val histogram: Double)
    
    fun calculateMACD(
        prices: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26,
        signalPeriod: Int = 9
    ): MACDResult? {
        if (prices.size < slowPeriod + signalPeriod) return null
        
        val fastEMA = calculateEMA(prices, fastPeriod) ?: return null
        val slowEMA = calculateEMA(prices, slowPeriod) ?: return null
        val macd = fastEMA - slowEMA
        
        // Calculate signal line (EMA of MACD)
        val macdLine = mutableListOf<Double>()
        for (i in slowPeriod - 1 until prices.size) {
            val fast = calculateEMA(prices.subList(0, i + 1), fastPeriod) ?: continue
            val slow = calculateEMA(prices.subList(0, i + 1), slowPeriod) ?: continue
            macdLine.add(fast - slow)
        }
        
        val signal = calculateEMA(macdLine, signalPeriod) ?: return null
        val histogram = macd - signal
        
        return MACDResult(macd, signal, histogram)
    }

    /**
     * Calculate Bollinger Bands
     */
    data class BollingerBands(val upper: Double, val middle: Double, val lower: Double)
    
    fun calculateBollingerBands(
        prices: List<Double>,
        period: Int = 20,
        stdDev: Double = 2.0
    ): BollingerBands? {
        if (prices.size < period) return null
        
        val recentPrices = prices.takeLast(period)
        val middle = recentPrices.average()
        
        val variance = recentPrices.map { (it - middle).pow(2) }.average()
        val standardDeviation = sqrt(variance)
        
        val upper = middle + (standardDeviation * stdDev)
        val lower = middle - (standardDeviation * stdDev)
        
        return BollingerBands(upper, middle, lower)
    }

    /**
     * Calculate Stochastic Oscillator
     */
    data class StochasticResult(val k: Double, val d: Double)
    
    fun calculateStochastic(
        candles: List<CandleData>,
        kPeriod: Int = 14,
        dPeriod: Int = 3,
        smoothing: Int = 3
    ): StochasticResult? {
        if (candles.size < kPeriod) return null
        
        val recentCandles = candles.takeLast(kPeriod)
        val highestHigh = recentCandles.maxOf { it.high }
        val lowestLow = recentCandles.minOf { it.low }
        val currentClose = recentCandles.last().close
        
        val k = if (highestHigh != lowestLow) {
            ((currentClose - lowestLow) / (highestHigh - lowestLow)) * 100.0
        } else {
            50.0
        }
        
        // Calculate %D (SMA of %K)
        val kValues = mutableListOf<Double>()
        for (i in kPeriod - 1 until candles.size) {
            val subset = candles.subList(maxOf(0, i - kPeriod + 1), i + 1)
            val high = subset.maxOf { it.high }
            val low = subset.minOf { it.low }
            val close = subset.last().close
            
            val kValue = if (high != low) {
                ((close - low) / (high - low)) * 100.0
            } else {
                50.0
            }
            kValues.add(kValue)
        }
        
        val d = if (kValues.size >= dPeriod) {
            kValues.takeLast(dPeriod).average()
        } else {
            k
        }
        
        return StochasticResult(k, d)
    }

    /**
     * Detect EMA crossover
     */
    fun detectEMACrossover(
        prices: List<Double>,
        shortPeriod: Int,
        longPeriod: Int
    ): CrossoverType {
        if (prices.size < longPeriod + 1) return CrossoverType.NONE
        
        val currentShort = calculateEMA(prices, shortPeriod)
        val currentLong = calculateEMA(prices, longPeriod)
        val prevShort = calculateEMA(prices.dropLast(1), shortPeriod)
        val prevLong = calculateEMA(prices.dropLast(1), longPeriod)
        
        if (currentShort == null || currentLong == null || prevShort == null || prevLong == null) {
            return CrossoverType.NONE
        }
        
        // Bullish crossover: short crosses above long
        if (prevShort <= prevLong && currentShort > currentLong) {
            return CrossoverType.BULLISH
        }
        
        // Bearish crossover: short crosses below long
        if (prevShort >= prevLong && currentShort < currentLong) {
            return CrossoverType.BEARISH
        }
        
        return CrossoverType.NONE
    }

    enum class CrossoverType {
        BULLISH,
        BEARISH,
        NONE
    }

    /**
     * Calculate VWAP slope to determine trend
     */
    fun calculateVWAPSlope(candles: List<CandleData>, period: Int = 10): Double? {
        if (candles.size < period) return null
        
        val recentCandles = candles.takeLast(period)
        val vwaps = mutableListOf<Double>()
        
        for (i in 0 until recentCandles.size) {
            val subset = recentCandles.subList(0, i + 1)
            calculateVWAP(subset)?.let { vwaps.add(it) }
        }
        
        if (vwaps.size < 2) return null
        
        // Simple slope calculation
        return (vwaps.last() - vwaps.first()) / vwaps.size
    }
}

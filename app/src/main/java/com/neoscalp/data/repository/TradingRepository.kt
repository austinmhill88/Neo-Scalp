package com.neoscalp.data.repository

import android.util.Log
import com.neoscalp.data.local.TradeDao
import com.neoscalp.data.model.*
import com.neoscalp.data.remote.AlpacaApiService
import com.neoscalp.data.remote.AlpacaWebSocketClient
import com.neoscalp.domain.model.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradingRepository @Inject constructor(
    private val tradeDao: TradeDao,
    private var alpacaApiService: AlpacaApiService? = null,
    private var webSocketClient: AlpacaWebSocketClient? = null
) {
    private val TAG = "TradingRepository"

    // Flows
    private val _accountStatus = MutableStateFlow<AccountStatus?>(null)
    val accountStatus: StateFlow<AccountStatus?> = _accountStatus.asStateFlow()

    private val _positions = MutableStateFlow<List<PositionInfo>>(emptyList())
    val positions: StateFlow<List<PositionInfo>> = _positions.asStateFlow()

    private val _marketData = MutableStateFlow<MarketSnapshot?>(null)
    val marketData: StateFlow<MarketSnapshot?> = _marketData.asStateFlow()

    fun getTradeHistory(): Flow<List<TradeEntry>> = tradeDao.getAllTrades()

    fun getOpenTrades(): Flow<List<TradeEntry>> = tradeDao.getOpenTrades()

    suspend fun insertTrade(trade: TradeEntry) = tradeDao.insertTrade(trade)

    suspend fun updateTrade(trade: TradeEntry) = tradeDao.updateTrade(trade)

    fun initializeAlpacaClient(apiKey: String, apiSecret: String, isPaper: Boolean) {
        webSocketClient = AlpacaWebSocketClient(apiKey, apiSecret, isPaper)
    }

    fun getWebSocketClient(): AlpacaWebSocketClient? = webSocketClient

    suspend fun getAccount(): Result<AccountResponse> {
        return try {
            val response = alpacaApiService?.getAccount()
            if (response?.isSuccessful == true && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch account: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching account", e)
            Result.failure(e)
        }
    }

    suspend fun validateSymbol(symbol: String): Result<AssetResponse> {
        return try {
            val response = alpacaApiService?.getAsset(symbol)
            if (response?.isSuccessful == true && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Invalid symbol: $symbol"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error validating symbol", e)
            Result.failure(e)
        }
    }

    suspend fun createOrder(orderRequest: OrderRequest): Result<OrderResponse> {
        return try {
            val response = alpacaApiService?.createOrder(orderRequest)
            if (response?.isSuccessful == true && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create order: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating order", e)
            Result.failure(e)
        }
    }

    suspend fun getPositions(): Result<List<PositionResponse>> {
        return try {
            val response = alpacaApiService?.getPositions()
            if (response?.isSuccessful == true && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch positions: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching positions", e)
            Result.failure(e)
        }
    }

    suspend fun closePosition(symbol: String): Result<OrderResponse> {
        return try {
            val response = alpacaApiService?.closePosition(symbol)
            if (response?.isSuccessful == true && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to close position: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error closing position", e)
            Result.failure(e)
        }
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            val response = alpacaApiService?.cancelOrder(orderId)
            if (response?.isSuccessful == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel order: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling order", e)
            Result.failure(e)
        }
    }

    fun updateAccountStatus(account: AccountResponse) {
        val status = AccountStatus(
            equity = account.equity.toDoubleOrNull() ?: 0.0,
            cash = account.cash.toDoubleOrNull() ?: 0.0,
            buyingPower = account.buyingPower.toDoubleOrNull() ?: 0.0,
            portfolioValue = account.portfolioValue.toDoubleOrNull() ?: 0.0,
            dayTradeCount = account.daytradeCount,
            isPatternDayTrader = account.patternDayTrader
        )
        _accountStatus.value = status
    }

    fun updateMarketData(snapshot: MarketSnapshot) {
        _marketData.value = snapshot
    }

    suspend fun getTodayPnL(): Double {
        val todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000)
        return tradeDao.getTodayPnL(todayStart) ?: 0.0
    }

    suspend fun getTradeStats(): Triple<Int, Int, Double> {
        val total = tradeDao.getTotalTradesCount()
        val wins = tradeDao.getWinningTradesCount()
        val winRate = if (total > 0) (wins.toDouble() / total) * 100 else 0.0
        return Triple(total, wins, winRate)
    }
}

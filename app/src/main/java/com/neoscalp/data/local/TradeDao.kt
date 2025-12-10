package com.neoscalp.data.local

import androidx.room.*
import com.neoscalp.domain.model.TradeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY entryTime DESC")
    fun getAllTrades(): Flow<List<TradeEntry>>

    @Query("SELECT * FROM trades WHERE status = 'open'")
    fun getOpenTrades(): Flow<List<TradeEntry>>

    @Query("SELECT * FROM trades WHERE symbol = :symbol ORDER BY entryTime DESC")
    fun getTradesBySymbol(symbol: String): Flow<List<TradeEntry>>

    @Query("SELECT * FROM trades WHERE entryTime BETWEEN :startTime AND :endTime")
    fun getTradesByDateRange(startTime: Long, endTime: Long): Flow<List<TradeEntry>>

    @Query("SELECT * FROM trades WHERE id = :id")
    suspend fun getTradeById(id: Long): TradeEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntry): Long

    @Update
    suspend fun updateTrade(trade: TradeEntry)

    @Delete
    suspend fun deleteTrade(trade: TradeEntry)

    @Query("DELETE FROM trades")
    suspend fun deleteAllTrades()

    @Query("SELECT SUM(profitLoss) FROM trades WHERE status = 'closed' AND entryTime > :todayStart")
    suspend fun getTodayPnL(todayStart: Long): Double?

    @Query("SELECT COUNT(*) FROM trades WHERE status = 'closed'")
    suspend fun getTotalTradesCount(): Int

    @Query("SELECT COUNT(*) FROM trades WHERE status = 'closed' AND profitLoss > 0")
    suspend fun getWinningTradesCount(): Int
}

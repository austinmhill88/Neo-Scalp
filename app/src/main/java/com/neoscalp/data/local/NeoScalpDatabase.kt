package com.neoscalp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neoscalp.domain.model.TradeEntry

@Database(
    entities = [TradeEntry::class],
    version = 1,
    exportSchema = false
)
abstract class NeoScalpDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
}

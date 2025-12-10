package com.neoscalp.data.model

import com.google.gson.annotations.SerializedName

// Account Information
data class AccountResponse(
    @SerializedName("id") val id: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("status") val status: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("cash") val cash: String,
    @SerializedName("portfolio_value") val portfolioValue: String,
    @SerializedName("buying_power") val buyingPower: String,
    @SerializedName("equity") val equity: String,
    @SerializedName("last_equity") val lastEquity: String,
    @SerializedName("long_market_value") val longMarketValue: String?,
    @SerializedName("short_market_value") val shortMarketValue: String?,
    @SerializedName("initial_margin") val initialMargin: String?,
    @SerializedName("maintenance_margin") val maintenanceMargin: String?,
    @SerializedName("daytrade_count") val daytradeCount: Int,
    @SerializedName("pattern_day_trader") val patternDayTrader: Boolean
)

// Asset Information
data class AssetResponse(
    @SerializedName("id") val id: String,
    @SerializedName("class") val assetClass: String,
    @SerializedName("exchange") val exchange: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("tradable") val tradable: Boolean,
    @SerializedName("marginable") val marginable: Boolean,
    @SerializedName("shortable") val shortable: Boolean,
    @SerializedName("easy_to_borrow") val easyToBorrow: Boolean,
    @SerializedName("fractionable") val fractionable: Boolean
)

// Order Request
data class OrderRequest(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("qty") val qty: String?,
    @SerializedName("notional") val notional: String?,
    @SerializedName("side") val side: String, // "buy" or "sell"
    @SerializedName("type") val type: String, // "market", "limit", "stop", "stop_limit"
    @SerializedName("time_in_force") val timeInForce: String, // "day", "gtc", "ioc", "fok"
    @SerializedName("limit_price") val limitPrice: String?,
    @SerializedName("stop_price") val stopPrice: String?,
    @SerializedName("client_order_id") val clientOrderId: String?,
    @SerializedName("extended_hours") val extendedHours: Boolean?
)

// Order Response
data class OrderResponse(
    @SerializedName("id") val id: String,
    @SerializedName("client_order_id") val clientOrderId: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("submitted_at") val submittedAt: String,
    @SerializedName("filled_at") val filledAt: String?,
    @SerializedName("expired_at") val expiredAt: String?,
    @SerializedName("canceled_at") val canceledAt: String?,
    @SerializedName("failed_at") val failedAt: String?,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("asset_class") val assetClass: String,
    @SerializedName("qty") val qty: String?,
    @SerializedName("notional") val notional: String?,
    @SerializedName("filled_qty") val filledQty: String,
    @SerializedName("filled_avg_price") val filledAvgPrice: String?,
    @SerializedName("order_type") val orderType: String,
    @SerializedName("side") val side: String,
    @SerializedName("time_in_force") val timeInForce: String,
    @SerializedName("limit_price") val limitPrice: String?,
    @SerializedName("stop_price") val stopPrice: String?,
    @SerializedName("status") val status: String,
    @SerializedName("extended_hours") val extendedHours: Boolean
)

// Position
data class PositionResponse(
    @SerializedName("asset_id") val assetId: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("exchange") val exchange: String,
    @SerializedName("asset_class") val assetClass: String,
    @SerializedName("avg_entry_price") val avgEntryPrice: String,
    @SerializedName("qty") val qty: String,
    @SerializedName("side") val side: String,
    @SerializedName("market_value") val marketValue: String,
    @SerializedName("cost_basis") val costBasis: String,
    @SerializedName("unrealized_pl") val unrealizedPl: String,
    @SerializedName("unrealized_plpc") val unrealizedPlpc: String,
    @SerializedName("unrealized_intraday_pl") val unrealizedIntradayPl: String,
    @SerializedName("unrealized_intraday_plpc") val unrealizedIntradayPlpc: String,
    @SerializedName("current_price") val currentPrice: String,
    @SerializedName("lastday_price") val lastdayPrice: String,
    @SerializedName("change_today") val changeToday: String
)

// Bar (Candle) Data
data class BarData(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

// WebSocket Messages
data class WebSocketAuth(
    val action: String = "auth",
    val key: String,
    val secret: String
)

data class WebSocketSubscribe(
    val action: String = "subscribe",
    val trades: List<String>? = null,
    val quotes: List<String>? = null,
    val bars: List<String>? = null
)

data class TradeUpdate(
    val symbol: String,
    val price: Double,
    val size: Long,
    val timestamp: Long
)

data class QuoteUpdate(
    val symbol: String,
    val bidPrice: Double,
    val bidSize: Long,
    val askPrice: Double,
    val askSize: Long,
    val timestamp: Long
)

data class BarUpdate(
    val symbol: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val timestamp: Long
)

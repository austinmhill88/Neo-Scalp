package com.neoscalp.data.remote

import com.neoscalp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AlpacaApiService {
    
    @GET("v2/account")
    suspend fun getAccount(): Response<AccountResponse>

    @GET("v2/assets/{symbol}")
    suspend fun getAsset(@Path("symbol") symbol: String): Response<AssetResponse>

    @GET("v2/assets")
    suspend fun getAssets(
        @Query("status") status: String = "active",
        @Query("asset_class") assetClass: String = "us_equity"
    ): Response<List<AssetResponse>>

    @POST("v2/orders")
    suspend fun createOrder(@Body order: OrderRequest): Response<OrderResponse>

    @GET("v2/orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 50
    ): Response<List<OrderResponse>>

    @GET("v2/orders/{order_id}")
    suspend fun getOrder(@Path("order_id") orderId: String): Response<OrderResponse>

    @DELETE("v2/orders/{order_id}")
    suspend fun cancelOrder(@Path("order_id") orderId: String): Response<Unit>

    @DELETE("v2/orders")
    suspend fun cancelAllOrders(): Response<List<OrderResponse>>

    @GET("v2/positions")
    suspend fun getPositions(): Response<List<PositionResponse>>

    @GET("v2/positions/{symbol}")
    suspend fun getPosition(@Path("symbol") symbol: String): Response<PositionResponse>

    @DELETE("v2/positions/{symbol}")
    suspend fun closePosition(
        @Path("symbol") symbol: String,
        @Query("qty") qty: String? = null,
        @Query("percentage") percentage: String? = null
    ): Response<OrderResponse>

    @DELETE("v2/positions")
    suspend fun closeAllPositions(
        @Query("cancel_orders") cancelOrders: Boolean = true
    ): Response<List<OrderResponse>>
}

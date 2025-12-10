package com.neoscalp.presentation.screens.dashboard

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neoscalp.domain.model.BotStatus
import com.neoscalp.domain.model.TradingMode
import com.neoscalp.presentation.theme.*
import com.neoscalp.service.TradingBotService

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val botStatus by viewModel.botStatus.collectAsState()
    val tradingMode by viewModel.tradingMode.collectAsState()
    val currentPrice by viewModel.currentPrice.collectAsState()
    val currentSymbol by viewModel.currentSymbol.collectAsState()
    val accountStatus by viewModel.accountStatus.collectAsState()
    val todayPnL by viewModel.todayPnL.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        Text(
            text = "NeoScalp",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = NeonGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Live Price Ticker
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = currentSymbol,
                    fontSize = 24.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${"%.2f".format(currentPrice)}",
                    fontSize = 36.sp,
                    color = if (todayPnL >= 0) ProfitGreen else LossRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Status Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mode",
                        fontSize = 12.sp,
                        color = NeonGreen.copy(alpha = 0.7f)
                    )
                    Text(
                        text = when (tradingMode) {
                            TradingMode.LONG -> "LONG"
                            TradingMode.SHORT -> "SHORT"
                            TradingMode.NEUTRAL -> "NEUTRAL"
                        },
                        fontSize = 18.sp,
                        color = when (tradingMode) {
                            TradingMode.LONG -> ProfitGreen
                            TradingMode.SHORT -> NeonRed
                            TradingMode.NEUTRAL -> NeonBlue
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // P&L Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Today P&L",
                        fontSize = 12.sp,
                        color = NeonGreen.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$${"%.2f".format(todayPnL)}",
                        fontSize = 18.sp,
                        color = if (todayPnL >= 0) ProfitGreen else LossRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Account Info
        accountStatus?.let { account ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Account",
                        fontSize = 18.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Equity", fontSize = 12.sp, color = NeonGreen.copy(alpha = 0.7f))
                            Text("$${"%.2f".format(account.equity)}", fontSize = 16.sp, color = NeonGreen)
                        }
                        Column {
                            Text("Cash", fontSize = 12.sp, color = NeonGreen.copy(alpha = 0.7f))
                            Text("$${"%.2f".format(account.cash)}", fontSize = 16.sp, color = NeonGreen)
                        }
                        Column {
                            Text("Buying Power", fontSize = 12.sp, color = NeonGreen.copy(alpha = 0.7f))
                            Text("$${"%.2f".format(account.buyingPower)}", fontSize = 16.sp, color = NeonGreen)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bot Control Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (botStatus == BotStatus.STOPPED) {
                        val intent = Intent(context, TradingBotService::class.java)
                        context.startForegroundService(intent)
                        viewModel.startBot()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = botStatus == BotStatus.STOPPED,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProfitGreen,
                    contentColor = Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Start Bot", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (botStatus == BotStatus.RUNNING) {
                        viewModel.stopBot()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = botStatus == BotStatus.RUNNING,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonRed,
                    contentColor = Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Stop Bot", fontWeight = FontWeight.Bold)
            }
        }

        // Status Text
        Text(
            text = when (botStatus) {
                BotStatus.RUNNING -> "Bot is running and monitoring markets..."
                BotStatus.STOPPED -> "Bot is stopped"
                BotStatus.PAUSED -> "Bot is paused"
                BotStatus.ERROR -> "Bot encountered an error"
            },
            fontSize = 14.sp,
            color = when (botStatus) {
                BotStatus.RUNNING -> ProfitGreen
                BotStatus.STOPPED -> NeonGreen.copy(alpha = 0.5f)
                BotStatus.PAUSED -> NeonBlue
                BotStatus.ERROR -> NeonRed
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Disclaimer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "⚠️ Trading involves significant risk of loss. This app is for educational purposes only. Not financial advice.",
                fontSize = 12.sp,
                color = NeonRed.copy(alpha = 0.8f),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

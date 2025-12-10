package com.neoscalp.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoscalp.data.repository.TradingRepository
import com.neoscalp.domain.model.AccountStatus
import com.neoscalp.domain.model.BotStatus
import com.neoscalp.domain.model.TradingMode
import com.neoscalp.domain.usecase.ScalpingBotEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val botEngine: ScalpingBotEngine,
    private val repository: TradingRepository
) : ViewModel() {

    private val _botStatus = MutableStateFlow(BotStatus.STOPPED)
    val botStatus: StateFlow<BotStatus> = _botStatus

    private val _tradingMode = MutableStateFlow(TradingMode.NEUTRAL)
    val tradingMode: StateFlow<TradingMode> = _tradingMode

    private val _currentPrice = MutableStateFlow(0.0)
    val currentPrice: StateFlow<Double> = _currentPrice

    private val _currentSymbol = MutableStateFlow("JNJ")
    val currentSymbol: StateFlow<String> = _currentSymbol

    private val _accountStatus = MutableStateFlow<AccountStatus?>(null)
    val accountStatus: StateFlow<AccountStatus?> = _accountStatus

    private val _todayPnL = MutableStateFlow(0.0)
    val todayPnL: StateFlow<Double> = _todayPnL

    init {
        viewModelScope.launch {
            // Collect bot status
            botEngine.botStatus.collect { status ->
                _botStatus.value = status
            }
        }

        viewModelScope.launch {
            // Collect trading mode
            botEngine.currentMode.collect { mode ->
                _tradingMode.value = mode
            }
        }

        viewModelScope.launch {
            // Collect account status
            repository.accountStatus.collect { status ->
                _accountStatus.value = status
            }
        }

        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load today's P&L
            _todayPnL.value = repository.getTodayPnL()

            // Try to load account info
            val accountResult = repository.getAccount()
            accountResult.onSuccess { account ->
                repository.updateAccountStatus(account)
            }
        }
    }

    fun startBot() {
        botEngine.start()
        _botStatus.value = BotStatus.RUNNING
    }

    fun stopBot() {
        botEngine.stop()
        _botStatus.value = BotStatus.STOPPED
    }

    fun updatePrice(price: Double) {
        _currentPrice.value = price
    }

    fun updateSymbol(symbol: String) {
        _currentSymbol.value = symbol
    }
}

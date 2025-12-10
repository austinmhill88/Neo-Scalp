# NeoScalp - Cyberpunk Trading Bot for Android

## Overview

NeoScalp is a modern, sci-fi themed Android application for automated and semi-automated scalping in stocks using the Alpaca API. The app features a cyberpunk/Matrix aesthetic with neon green UI elements and provides real-time market data, technical analysis, and automated trading capabilities.

## Features

### Core Functionality
- **Real-time Market Data**: WebSocket streaming for live price updates
- **Technical Indicators**: EMA, VWAP, RSI, MACD, Bollinger Bands, Stochastic Oscillator
- **Automated Trading Bot**: Smart signal detection with confluence-based entry/exit logic
- **Paper & Live Trading**: Safe paper trading mode for testing strategies
- **Risk Management**: Position sizing based on account equity and risk percentage
- **Foreground Service**: Continuous operation even when app is in background

### User Interface
- **Dashboard**: Live price ticker, mode indicator, P&L tracking, bot controls
- **Symbols**: Manage active trading symbol and watchlist
- **Parameters**: Adjust indicator periods and risk settings with 0.1 increment precision
- **Settings**: Configure Alpaca API credentials and trading mode
- **Logs**: View trade history with export to CSV capability
- **Backtest**: Test strategies on historical data (placeholder for future implementation)

### Technical Specifications
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Hilt dependency injection
- **Networking**: Retrofit 2.11.0 for REST, OkHttp 5.0.0 for WebSocket
- **Database**: Room 2.6.1 for trade history
- **Security**: EncryptedSharedPreferences for API keys
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)

## Setup Instructions

### Prerequisites
1. Android Studio (latest stable version)
2. JDK 17
3. Alpaca Trading Account (for API keys)
   - Paper trading: https://app.alpaca.markets/paper/dashboard/overview
   - Live trading: https://app.alpaca.markets/

### Installation

1. Clone the repository:
```bash
git clone https://github.com/austinmhill88/Neo-Scalp.git
cd Neo-Scalp
```

2. Open the project in Android Studio

3. Sync Gradle files (Android Studio will prompt automatically)

4. Connect an Android device or start an emulator

5. Build and run the app:
   - For debug: Click "Run" button or use `Shift + F10`
   - For release: Build > Generate Signed Bundle/APK

### Configuration

1. Launch the app
2. Navigate to Settings tab
3. Enter your Alpaca API credentials:
   - API Key
   - API Secret
4. Choose trading mode (Paper/Live)
5. Test connection to verify credentials
6. Navigate to Parameters to adjust trading settings
7. Start the bot from Dashboard when ready

## Usage

### Starting the Bot

1. Ensure API credentials are configured in Settings
2. Adjust parameters if needed (default values are conservative)
3. Select your trading symbol in the Symbols tab
4. Go to Dashboard and tap "Start Bot"
5. The bot will run as a foreground service with notifications

### Monitoring

- **Dashboard**: View current price, mode (Long/Short/Neutral), and P&L
- **Notifications**: Receive alerts for entry/exit signals
- **Logs**: Review trade history and export for analysis

### Safety Features

- Paper trading mode by default
- Legal disclaimer on sensitive actions
- Risk management with configurable limits
- Manual confirmation for live trades (in semi-auto mode)

## Architecture

### Data Layer
- **Remote**: Alpaca API service and WebSocket client
- **Local**: Room database for trade history
- **Repository**: Unified data access layer

### Domain Layer
- **Models**: Domain-specific data classes
- **Use Cases**: Business logic (ScalpingBotEngine)

### Presentation Layer
- **Screens**: Jetpack Compose UI screens
- **ViewModels**: State management with StateFlow
- **Navigation**: Compose Navigation with bottom bar

### Dependency Injection
- Hilt for providing dependencies
- Scoped appropriately (Singleton, ViewModel)

## Technical Indicators

### Entry Signal Detection
The bot uses confluence-based signal detection requiring at least 3 indicators:

**Long Signals**:
- RSI < 30 (oversold)
- Stochastic < 20 (oversold)
- Price above VWAP
- MACD histogram > 0 (bullish)
- Price at Bollinger lower band
- EMA 9/21 bullish crossover

**Short Signals**:
- RSI > 70 (overbought)
- Stochastic > 80 (overbought)
- Price below VWAP
- MACD histogram < 0 (bearish)
- Price at Bollinger upper band
- EMA 9/21 bearish crossover

### Exit Signal Detection
- Target profit percentage reached
- Stop loss percentage reached
- Reversal signals (MACD divergence, RSI extremes)
- End of trading session

## Security

- API keys stored in EncryptedSharedPreferences
- No sensitive data in logs or backups
- Network requests use HTTPS
- Backup/restore excludes encrypted data

## Disclaimer

⚠️ **IMPORTANT**: Trading involves significant risk of loss. This app is for educational purposes only. You should consult a financial professional before trading. This is not financial advice.

The developer is not responsible for any financial losses incurred while using this application.

## License

This is a personal-use application. Not intended for distribution on Google Play Store.

## Troubleshooting

### Common Issues

1. **Bot not starting**: Check API credentials in Settings and test connection
2. **No price updates**: Verify internet connection and WebSocket is connected
3. **Orders not executing**: Ensure sufficient buying power and check Alpaca account status
4. **Notifications not showing**: Grant notification permission in Android settings

### Support

For issues or questions, please open an issue on GitHub.

## Roadmap

- [ ] Complete backtesting implementation
- [ ] Add voice alerts with TTS
- [ ] Implement live chart with MPAndroidChart
- [ ] Add battery optimization detection
- [ ] Implement offline mode with auto-reconnect
- [ ] Add more technical indicators
- [ ] Enhanced P&L reporting and analytics
- [ ] Trade journal with notes

## Credits

Developed using:
- Alpaca Trading API
- Jetpack Compose
- Kotlin Coroutines
- Hilt
- Room
- Retrofit
- OkHttp

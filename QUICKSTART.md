# NeoScalp Quick Start Guide

## Initial Setup (5 minutes)

### 1. Get Alpaca API Keys

1. Visit https://app.alpaca.markets/signup
2. Create a free paper trading account
3. Navigate to "API Keys" section
4. Generate new API key pair
5. Save your API Key and Secret Key (you'll need these in the app)

### 2. Build the App

**Option A: Using Android Studio (Recommended)**
1. Open Android Studio
2. Click "Open" and select the Neo-Scalp folder
3. Wait for Gradle sync to complete (may take a few minutes first time)
4. Connect your Android device via USB or start an emulator
5. Click the green "Run" button (or press Shift+F10)

**Option B: Using Command Line**
```bash
cd /path/to/Neo-Scalp
./gradlew assembleDebug
# APK will be in: app/build/outputs/apk/debug/app-debug.apk
```

### 3. First Launch Configuration

1. **Launch NeoScalp** on your device
2. **Accept Permissions** when prompted (notifications)
3. Navigate to **Settings tab** (bottom navigation)
4. Enter your Alpaca credentials:
   - Paste your API Key
   - Paste your API Secret
   - Ensure "Paper Trading Mode" is ON (toggle should be off/left position)
5. Tap **"Save Credentials"**
6. Tap **"Test Connection"** to verify (should show success)

### 4. Start Trading (Paper Mode)

1. Navigate to **Symbols tab**
   - Default symbol is "JNJ" (Johnson & Johnson)
   - You can change to other symbols like "MSFT", "AAPL", "TSLA"
   
2. Navigate to **Parameters tab** (optional)
   - Review default settings (they are conservative)
   - Adjust risk percentage if desired (default 1%)
   - Tap "Save Parameters" if you made changes

3. Navigate to **Dashboard tab**
   - Review the disclaimer
   - Tap **"Start Bot"**
   - Bot will begin monitoring the market
   - Watch for signals in the notification bar

### 5. Monitoring Your Bot

**Dashboard View:**
- **Top Card**: Live symbol price updates
- **Mode Card**: Shows if bot is in LONG, SHORT, or NEUTRAL mode
- **P&L Card**: Today's profit/loss
- **Account Card**: Your equity, cash, buying power

**Notifications:**
- Bot status updates in notification drawer
- Entry/exit signals appear as separate notifications

**Logs Tab:**
- View all executed trades
- Export to CSV for analysis

## Default Bot Behavior

The bot operates with these default settings:

**Entry Logic (requires 3+ signals):**
- RSI < 30 (oversold) or > 70 (overbought)
- Stochastic extremes
- Price relationship to VWAP
- MACD crossovers
- Bollinger Band touches

**Exit Logic:**
- Target: +0.5% profit
- Stop: -0.3% loss
- Reversal signals detected

**Risk Management:**
- Max risk per trade: 1% of equity
- Position sizing automatically calculated
- Daily loss limits (default 3%)

## Safety Features

✅ **Paper Trading Default**: No real money at risk initially

✅ **Manual Override**: Stop bot anytime from Dashboard

✅ **Encrypted Storage**: API keys stored securely

✅ **Legal Disclaimers**: Clear warnings before any action

## Troubleshooting

### Bot Won't Start
- Check Settings: API keys saved correctly
- Test Connection in Settings tab
- Check internet connection
- Verify Alpaca account status online

### No Price Updates
- Ensure WebSocket is connecting (check logs)
- Verify symbol is valid (not all symbols supported)
- Check if market is open (US market hours: 9:30 AM - 4:00 PM ET)

### App Crashes
- Check Android version (need Android 8.0+)
- Clear app data and reconfigure
- Check logcat for error details

### Notifications Not Working
- Grant notification permission in Android settings
- Check "Do Not Disturb" is off
- Ensure battery optimization is disabled for NeoScalp

## Going Live (Advanced)

⚠️ **WARNING: Only proceed when you're comfortable with paper trading results**

1. Fund your Alpaca account (switch to live trading account)
2. Generate **live** API keys (separate from paper keys)
3. In NeoScalp Settings:
   - Enter your LIVE API keys
   - Toggle "Paper Trading Mode" to OFF (toggle right)
   - Carefully read the warning
   - Test connection
4. Start small: Test with minimal position sizes
5. Monitor closely: Watch the first few trades carefully

## Best Practices

1. **Start Small**: Begin with paper trading for at least 1-2 weeks
2. **Test Parameters**: Adjust settings in Parameters tab, observe results
3. **Review Logs**: Check trade history regularly
4. **Monitor Actively**: Don't rely 100% on automation initially
5. **Use Backtest**: Test strategies on historical data first (when implemented)
6. **Set Limits**: Configure daily loss limits to protect capital
7. **Market Hours**: Bot works best during active US market hours
8. **News Events**: Pause bot during major economic announcements

## Next Steps

1. Monitor your first few paper trades
2. Experiment with different symbols
3. Adjust parameters based on performance
4. Review the full README.md for architecture details
5. Check trade logs and analyze patterns

## Support

- Review Build Instructions document for detailed specifications
- Check README.md for technical architecture
- Open GitHub issues for bugs or questions

---

**Remember**: This is educational software. Always practice responsible trading and never risk more than you can afford to lose.

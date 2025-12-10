package com.neoscalp.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoscalp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var apiKey by remember { mutableStateOf("") }
    var apiSecret by remember { mutableStateOf("") }
    var isPaperMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = NeonGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Alpaca API Credentials",
                    fontSize = 20.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key", color = NeonGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NeonGreen,
                        unfocusedTextColor = NeonGreen,
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = NeonGreen.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = apiSecret,
                    onValueChange = { apiSecret = it },
                    label = { Text("API Secret", color = NeonGreen) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NeonGreen,
                        unfocusedTextColor = NeonGreen,
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = NeonGreen.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Save credentials */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Credentials", fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Trading Mode",
                    fontSize = 20.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isPaperMode) "Paper Trading (Safe)" else "Live Trading",
                        fontSize = 16.sp,
                        color = if (isPaperMode) NeonGreen else NeonRed
                    )
                    Switch(
                        checked = !isPaperMode,
                        onCheckedChange = { isPaperMode = !it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonRed,
                            checkedTrackColor = NeonRed.copy(alpha = 0.5f),
                            uncheckedThumbColor = NeonGreen,
                            uncheckedTrackColor = NeonGreen.copy(alpha = 0.5f)
                        )
                    )
                }

                if (!isPaperMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ WARNING: Live trading mode will execute real trades with real money!",
                        fontSize = 12.sp,
                        color = NeonRed,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Connection",
                    fontSize = 20.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { /* Test connection */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonBlue,
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Test Connection", fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About",
                    fontSize = 20.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "NeoScalp v1.0",
                    fontSize = 14.sp,
                    color = NeonGreen.copy(alpha = 0.7f)
                )
                Text(
                    text = "Cyberpunk Trading Bot",
                    fontSize = 12.sp,
                    color = NeonGreen.copy(alpha = 0.5f)
                )
            }
        }
    }
}

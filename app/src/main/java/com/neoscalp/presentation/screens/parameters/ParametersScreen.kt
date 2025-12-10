package com.neoscalp.presentation.screens.parameters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoscalp.presentation.theme.*

@Composable
fun ParametersScreen() {
    var ema9Period by remember { mutableStateOf(9.0f) }
    var ema21Period by remember { mutableStateOf(21.0f) }
    var rsiOversold by remember { mutableStateOf(30.0f) }
    var rsiOverbought by remember { mutableStateOf(70.0f) }
    var targetPercent by remember { mutableStateOf(0.5f) }
    var stopPercent by remember { mutableStateOf(0.3f) }
    var maxRisk by remember { mutableStateOf(1.0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Parameters",
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
                Text("EMA Settings", fontSize = 20.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("EMA 9 Period: ${"%.1f".format(ema9Period)}", color = NeonGreen)
                Slider(
                    value = ema9Period,
                    onValueChange = { ema9Period = it },
                    valueRange = 5f..15f,
                    steps = 100,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )

                Text("EMA 21 Period: ${"%.1f".format(ema21Period)}", color = NeonGreen)
                Slider(
                    value = ema21Period,
                    onValueChange = { ema21Period = it },
                    valueRange = 15f..30f,
                    steps = 150,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )
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
                Text("RSI Settings", fontSize = 20.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("RSI Oversold: ${"%.1f".format(rsiOversold)}", color = NeonGreen)
                Slider(
                    value = rsiOversold,
                    onValueChange = { rsiOversold = it },
                    valueRange = 20f..40f,
                    steps = 200,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )

                Text("RSI Overbought: ${"%.1f".format(rsiOverbought)}", color = NeonGreen)
                Slider(
                    value = rsiOverbought,
                    onValueChange = { rsiOverbought = it },
                    valueRange = 60f..80f,
                    steps = 200,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )
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
                Text("Risk Management", fontSize = 20.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Target %: ${"%.1f".format(targetPercent)}", color = NeonGreen)
                Slider(
                    value = targetPercent,
                    onValueChange = { targetPercent = it },
                    valueRange = 0.3f..1.0f,
                    steps = 70,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )

                Text("Stop %: ${"%.1f".format(stopPercent)}", color = NeonGreen)
                Slider(
                    value = stopPercent,
                    onValueChange = { stopPercent = it },
                    valueRange = 0.2f..0.5f,
                    steps = 30,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )

                Text("Max Risk %: ${"%.1f".format(maxRisk)}", color = NeonGreen)
                Slider(
                    value = maxRisk,
                    onValueChange = { maxRisk = it },
                    valueRange = 0.5f..2.0f,
                    steps = 150,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen
                    )
                )
            }
        }

        Button(
            onClick = { /* Save parameters */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                contentColor = Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Save Parameters", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

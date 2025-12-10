package com.neoscalp.presentation.screens.symbols

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoscalp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolsScreen() {
    var symbolInput by remember { mutableStateOf("JNJ") }
    var watchlist by remember { mutableStateOf(listOf("JNJ", "WMT", "KO", "PG", "MSFT")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Symbols",
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
                    text = "Active Symbol",
                    fontSize = 18.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = symbolInput,
                    onValueChange = { symbolInput = it.uppercase() },
                    label = { Text("Symbol", color = NeonGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NeonGreen,
                        unfocusedTextColor = NeonGreen,
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = NeonGreen.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* Validate and set symbol */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Set Active Symbol", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Watchlist",
            fontSize = 24.sp,
            color = NeonGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        watchlist.forEach { symbol ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = symbol, fontSize = 18.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                    Text(text = "Active", fontSize = 14.sp, color = if (symbol == symbolInput) ProfitGreen else NeonGreen.copy(alpha = 0.5f))
                }
            }
        }
    }
}

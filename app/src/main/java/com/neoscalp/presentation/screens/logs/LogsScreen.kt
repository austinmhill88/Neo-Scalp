package com.neoscalp.presentation.screens.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoscalp.presentation.theme.*

@Composable
fun LogsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Trade Logs",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = NeonGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Export CSV */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonBlue,
                    contentColor = Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Export CSV", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { /* Clear logs */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonRed,
                    contentColor = Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Clear Logs", fontWeight = FontWeight.Bold)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "No trade logs available yet.",
                        fontSize = 14.sp,
                        color = NeonGreen.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

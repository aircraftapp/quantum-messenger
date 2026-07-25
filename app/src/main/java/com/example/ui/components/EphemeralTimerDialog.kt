package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class EphemeralPreset(
    val label: String,
    val seconds: Long
)

@Composable
fun EphemeralTimerDialog(
    currentSeconds: Long,
    onDismiss: () -> Unit,
    onSelectSeconds: (Long) -> Unit
) {
    val presets = listOf(
        EphemeralPreset("Off (Messages Persist)", 0L),
        EphemeralPreset("5 Seconds (Ultra Ephemeral)", 5L),
        EphemeralPreset("30 Seconds", 30L),
        EphemeralPreset("5 Minutes", 300L),
        EphemeralPreset("1 Hour", 3600L),
        EphemeralPreset("24 Hours (1 Day)", 86400L)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = AlertCrimson)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Automated Ephemeral Timer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Messages in this channel will auto-shred automatically on both sender and recipient phones after expiry.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                for (preset in presets) {
                    val isSelected = preset.seconds == currentSeconds
                    Surface(
                        color = if (isSelected) AlertCrimson.copy(alpha = 0.2f) else CardSlate,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSeconds(preset.seconds) }
                            .testTag("ephemeral_preset_${preset.seconds}")
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = preset.label,
                                color = if (isSelected) AlertCrimson else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AlertCrimson)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = DarkSlate
    )
}

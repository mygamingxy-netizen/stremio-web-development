package com.uragontv.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.ObsidianBlack
import com.uragontv.app.ui.theme.SoftAntiqueGold
import com.uragontv.app.ui.theme.TextPrimary

@Composable
fun TvKeyboard(
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("A", "B", "C", "D", "E", "F", "1", "2", "3"),
        listOf("G", "H", "I", "J", "K", "L", "4", "5", "6"),
        listOf("M", "N", "O", "P", "Q", "R", "7", "8", "9"),
        listOf("S", "T", "U", "V", "W", "X", "Y", "Z", "0")
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { rowKeys ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowKeys.forEach { key ->
                    TvFocusableCard(
                        onClick = { onKeyPress(key) },
                        modifier = Modifier
                            .width(44.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        scaleFactor = 1.15f
                    ) { isFocused ->
                        Box(
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = if (isFocused) SoftAntiqueGold else TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action row: SPACE, BACKSPACE, CLEAR
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            TvFocusableCard(
                onClick = { onKeyPress(" ") },
                modifier = Modifier
                    .width(130.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp)),
                scaleFactor = 1.1f
            ) { isFocused ->
                Box(
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SPACE",
                        color = if (isFocused) SoftAntiqueGold else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TvFocusableCard(
                onClick = onBackspace,
                modifier = Modifier
                    .width(130.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp)),
                scaleFactor = 1.1f
            ) { isFocused ->
                Box(
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌫ DELETE",
                        color = if (isFocused) SoftAntiqueGold else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TvFocusableCard(
                onClick = onClear,
                modifier = Modifier
                    .width(90.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp)),
                scaleFactor = 1.1f
            ) { isFocused ->
                Box(
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CLEAR",
                        color = if (isFocused) SoftAntiqueGold else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

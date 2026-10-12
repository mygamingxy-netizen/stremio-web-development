package com.uragontv.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.uragontv.app.ui.theme.DarkCharcoal
import com.uragontv.app.ui.theme.GrayBorder
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.SurfaceElevated

@Composable
fun TvFocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    scaleFactor: Float = 1.08f,
    focusedBorderColor: Color = MetallicGold,
    unfocusedBorderColor: Color = GrayBorder.copy(alpha = 0.5f),
    focusedBackgroundColor: Color = SurfaceElevated,
    unfocusedBackgroundColor: Color = DarkCharcoal,
    borderWidth: Dp = 2.dp,
    onFocusChange: ((Boolean) -> Unit)? = null,
    content: @Composable (isFocused: Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleFactor else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "TvCardScale"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isFocused) 16.dp else 4.dp,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .background(if (isFocused) focusedBackgroundColor else unfocusedBackgroundColor)
            .border(
                width = if (isFocused) borderWidth else 1.dp,
                color = if (isFocused) focusedBorderColor else unfocusedBorderColor,
                shape = shape
            )
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChange?.invoke(focusState.isFocused)
            }
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        content(isFocused)
    }
}

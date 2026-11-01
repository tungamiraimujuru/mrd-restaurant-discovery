package com.mrd.discovery.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MrDTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MrDLightColorScheme,
        typography = MrDTypography,
        shapes = MrDShapes,
        content = content,
    )
}

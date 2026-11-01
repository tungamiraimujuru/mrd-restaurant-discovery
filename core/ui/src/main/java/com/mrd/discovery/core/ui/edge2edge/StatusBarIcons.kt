package com.mrd.discovery.core.ui.edge2edge

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Switches the status bar icons while this composable is in the composition: [lightIcons] = true
 * for a screen with a dark image behind the bar. The previous setting is restored on exit.
 */
@Composable
fun StatusBarIconsEffect(lightIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(view, lightIcons) {
        val window = view.context.findActivity()?.window
        if (window == null) {
            onDispose {}
        } else {
            val controller = WindowCompat.getInsetsController(window, view)
            val previous = controller.isAppearanceLightStatusBars
            controller.isAppearanceLightStatusBars = !lightIcons
            onDispose { controller.isAppearanceLightStatusBars = previous }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

package com.mrd.discovery.core.ui.edge2edge

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

/**
 * The only place allowed to call the platform enableEdgeToEdge().
 * Must run in onCreate() BEFORE super.onCreate(); BaseActivity does this for every screen.
 *
 * The app is light-only, so bar icons stay dark even when the device is in dark mode.
 * A screen that draws a dark image behind the status bar flips this locally.
 */
fun ComponentActivity.displayEdgeToEdge() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
    )
}

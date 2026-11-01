package com.mrd.discovery.core.ui.edge2edge

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

/**
 * The only place allowed to call the platform enableEdgeToEdge().
 * Must run in onCreate() BEFORE super.onCreate(); BaseActivity does this for every screen.
 */
fun ComponentActivity.displayEdgeToEdge() {
    enableEdgeToEdge()
}

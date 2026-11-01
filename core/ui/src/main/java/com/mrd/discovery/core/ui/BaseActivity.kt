package com.mrd.discovery.core.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mrd.discovery.core.ui.edge2edge.displayEdgeToEdge

/**
 * Base class for every Activity in the app. Applies edge-to-edge before the window is created,
 * so no screen has to remember to. Cross-cutting concerns (for example session handling)
 * would be added here rather than in individual Activities.
 */
abstract class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        displayEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}

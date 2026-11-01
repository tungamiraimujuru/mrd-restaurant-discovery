package com.mrd.discovery

import android.os.Bundle
import androidx.activity.compose.setContent
import com.mrd.discovery.core.designsystem.theme.MrDTheme
import com.mrd.discovery.core.ui.BaseActivity
import com.mrd.discovery.navigation.MrDNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MrDTheme {
                MrDNavHost()
            }
        }
    }
}

package com.mrd.discovery

import android.os.Bundle
import androidx.activity.compose.setContent
import com.mrd.discovery.core.designsystem.theme.MrDTheme
import com.mrd.discovery.core.network.fake.FakeBackendConfig
import com.mrd.discovery.core.ui.BaseActivity
import com.mrd.discovery.debug.DebugOverlay
import com.mrd.discovery.navigation.MrDNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var fakeBackend: FakeBackendConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MrDTheme {
                DebugOverlay(fakeBackend = fakeBackend) {
                    MrDNavHost()
                }
            }
        }
    }
}

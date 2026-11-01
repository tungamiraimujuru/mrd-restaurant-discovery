package com.mrd.discovery.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

private const val DIMMED_ALPHA = 0.85f
private const val DIMMED_SATURATION = 0.2f
private val DimmedFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(DIMMED_SATURATION) })

/**
 * Remote image that is safe when the URL is missing, loading, or fails (for example offline and
 * not yet cached): a neutral block is shown instead. [dimmed] mutes it for closed restaurants.
 */
@Composable
fun MrDImage(url: String?, modifier: Modifier = Modifier, dimmed: Boolean = false) {
    val blockColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val block = remember(blockColor) { ColorPainter(blockColor) }
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier,
        placeholder = block,
        error = block,
        fallback = block,
        contentScale = ContentScale.Crop,
        alpha = if (dimmed) DIMMED_ALPHA else 1f,
        colorFilter = if (dimmed) DimmedFilter else null,
    )
}

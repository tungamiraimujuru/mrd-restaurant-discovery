package com.mrd.discovery.core.designsystem.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

private const val HEART_PATH =
    "M12,21.35l-1.45,-1.32C5.4,15.36 2,12.28 2,8.5 2,5.42 4.42,3 7.5,3c1.74,0 3.41,0.81 4.5,2.09" +
        "C13.09,3.81 14.76,3 16.5,3 19.58,3 22,5.42 22,8.5c0,3.78 -3.4,6.86 -8.55,11.54L12,21.35z"
private const val ARROW_BACK_PATH = "M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z"
private const val OUTLINE_STROKE_WIDTH = 2f

/** Drawn in code so the design system needs no icon library. Tinted by the component using them. */
internal object MrDIcons {
    val HeartFilled: ImageVector = icon(HEART_PATH, filled = true)
    val HeartOutline: ImageVector = icon(HEART_PATH, filled = false)
    val ArrowBack: ImageVector = icon(ARROW_BACK_PATH, filled = true)
}

private fun icon(pathData: String, filled: Boolean): ImageVector = ImageVector.Builder(
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).addPath(
    pathData = addPathNodes(pathData),
    fill = if (filled) SolidColor(Color.Black) else null,
    stroke = if (filled) null else SolidColor(Color.Black),
    strokeLineWidth = if (filled) 0f else OUTLINE_STROKE_WIDTH,
).build()

package com.mrd.discovery.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme

private val ButtonElevation = 2.dp
private const val BUTTON_BACKGROUND_ALPHA = 0.9f

/** [contentDescription] should describe the action ("Add to favorites"), so it flips with the state. */
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    CircleIconButton(
        icon = if (isFavorite) MrDIcons.HeartFilled else MrDIcons.HeartOutline,
        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun BackButton(contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(
        icon = MrDIcons.ArrowBack,
        tint = MaterialTheme.colorScheme.onSurface,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    tint: Color,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = BUTTON_BACKGROUND_ALPHA),
        shadowElevation = ButtonElevation,
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IconButtonsPreview() {
    MrDTheme {
        Row(modifier = Modifier.padding(MrDSpacing.md)) {
            BackButton(contentDescription = "Back", onClick = {})
            FavoriteButton(
                isFavorite = false,
                onClick = {},
                contentDescription = "Add to favorites",
                modifier = Modifier.padding(start = MrDSpacing.sm),
            )
            FavoriteButton(
                isFavorite = true,
                onClick = {},
                contentDescription = "Remove from favorites",
                modifier = Modifier.padding(start = MrDSpacing.sm),
            )
        }
    }
}

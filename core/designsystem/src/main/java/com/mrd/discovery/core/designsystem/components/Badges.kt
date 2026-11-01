package com.mrd.discovery.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme

/** Green when open, red when closed. The caller supplies the localized text. */
@Composable
fun StatusBadge(text: String, isOpen: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = if (isOpen) colors.tertiaryContainer else colors.errorContainer,
        contentColor = if (isOpen) colors.onTertiaryContainer else colors.onErrorContainer,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = MrDSpacing.sm, vertical = MrDSpacing.xs),
        )
    }
}

/** [text] is the already formatted rating, e.g. "4.6". */
@Composable
fun RatingPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryFixed,
        contentColor = MaterialTheme.colorScheme.onPrimaryFixedVariant,
    ) {
        Text(
            text = "★ $text",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = MrDSpacing.sm, vertical = MrDSpacing.xs),
        )
    }
}

/** Small neutral chip for facts such as the ETA or the delivery fee. */
@Composable
fun InfoChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = MrDSpacing.sm, vertical = MrDSpacing.xs),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgesPreview() {
    MrDTheme {
        Row(modifier = Modifier.padding(MrDSpacing.md)) {
            StatusBadge(text = "OPEN", isOpen = true)
            StatusBadge(text = "CLOSED", isOpen = false, modifier = Modifier.padding(start = MrDSpacing.sm))
            RatingPill(text = "4.6", modifier = Modifier.padding(start = MrDSpacing.sm))
            InfoChip(text = "32 min", modifier = Modifier.padding(start = MrDSpacing.sm))
        }
    }
}

package com.mrd.discovery.feature.discovery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrd.discovery.core.designsystem.components.FavoriteButton
import com.mrd.discovery.core.designsystem.components.InfoChip
import com.mrd.discovery.core.designsystem.components.MrDImage
import com.mrd.discovery.core.designsystem.components.RatingPill
import com.mrd.discovery.core.designsystem.components.StatusBadge
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme
import com.mrd.discovery.core.ui.format.DeliveryFee

private val ImageHeight = 176.dp

@Composable
internal fun RestaurantCard(
    restaurant: RestaurantUiModel,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(MrDSpacing.border, MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        CardImage(restaurant = restaurant, onFavoriteClick = onFavoriteClick)
        CardDetails(restaurant)
    }
}

@Composable
private fun CardImage(restaurant: RestaurantUiModel, onFavoriteClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(ImageHeight)) {
        MrDImage(
            url = restaurant.imageUrl,
            dimmed = !restaurant.isOpen,
            modifier = Modifier.fillMaxSize(),
        )
        StatusBadge(
            text = stringResource(if (restaurant.isOpen) R.string.restaurant_open else R.string.restaurant_closed),
            isOpen = restaurant.isOpen,
            modifier = Modifier.align(Alignment.TopStart).padding(MrDSpacing.sm),
        )
        FavoriteButton(
            isFavorite = restaurant.isFavorite,
            onClick = onFavoriteClick,
            contentDescription = stringResource(
                if (restaurant.isFavorite) R.string.restaurant_favorite_remove else R.string.restaurant_favorite_add,
            ),
            modifier = Modifier.align(Alignment.TopEnd).padding(MrDSpacing.xs),
        )
        restaurant.etaMinutes?.let { minutes ->
            InfoChip(
                text = stringResource(R.string.restaurant_eta, minutes),
                modifier = Modifier.align(Alignment.BottomEnd).padding(MrDSpacing.sm),
            )
        }
    }
}

@Composable
private fun CardDetails(restaurant: RestaurantUiModel) {
    Column(
        modifier = Modifier.padding(MrDSpacing.md),
        verticalArrangement = Arrangement.spacedBy(MrDSpacing.xs),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
            restaurant.ratingText?.let { RatingPill(text = it) }
        }
        if (restaurant.cuisines.isNotEmpty()) {
            Text(
                text = restaurant.cuisines,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DeliveryFeeLabel(restaurant.deliveryFee)
    }
}

@Composable
private fun DeliveryFeeLabel(fee: DeliveryFee) {
    when (fee) {
        DeliveryFee.Unknown -> Unit

        DeliveryFee.Free -> Text(
            text = stringResource(R.string.restaurant_fee_free),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )

        is DeliveryFee.Paid -> Text(
            text = stringResource(R.string.restaurant_fee_paid, fee.formatted),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RestaurantCardPreview() {
    MrDTheme {
        Column(
            modifier = Modifier.padding(MrDSpacing.md),
            verticalArrangement = Arrangement.spacedBy(MrDSpacing.md),
        ) {
            RestaurantCard(restaurant = previewRestaurant(), onClick = {}, onFavoriteClick = {})
            RestaurantCard(
                restaurant = previewRestaurant(
                    id = "r-1002",
                    name = "Sushi on Sea Point",
                    isOpen = false,
                    fee = DeliveryFee.Free,
                ).copy(isFavorite = true),
                onClick = {},
                onFavoriteClick = {},
            )
        }
    }
}

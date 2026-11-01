package com.mrd.discovery.feature.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrd.discovery.core.designsystem.components.BackButton
import com.mrd.discovery.core.designsystem.components.EmptyView
import com.mrd.discovery.core.designsystem.components.ErrorView
import com.mrd.discovery.core.designsystem.components.FavoriteButton
import com.mrd.discovery.core.designsystem.components.InfoChip
import com.mrd.discovery.core.designsystem.components.LoadingView
import com.mrd.discovery.core.designsystem.components.MrDImage
import com.mrd.discovery.core.designsystem.components.RatingPill
import com.mrd.discovery.core.designsystem.components.StatusBadge
import com.mrd.discovery.core.designsystem.theme.MrDSpacing
import com.mrd.discovery.core.designsystem.theme.MrDTheme
import com.mrd.discovery.core.ui.edge2edge.StatusBarIconsEffect
import com.mrd.discovery.core.ui.format.DeliveryFee

private val HeroHeight = 256.dp
private val CardOverlap = 24.dp
private val HeroScrim = Brush.verticalGradient(
    0f to Color.Black.copy(alpha = 0.5f),
    0.4f to Color.Transparent,
    1f to Color.Black.copy(alpha = 0.25f),
)

@Composable
internal fun DetailsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DetailsScreenContent(
        state = state,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        onFavoriteClick = viewModel::onFavoriteClick,
        modifier = modifier,
    )
}

/** Stateless, so previews and tests can drive every state directly. */
@Composable
internal fun DetailsScreenContent(
    state: DetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Only the hero has a dark image behind the status bar; other states sit on the light background.
    StatusBarIconsEffect(lightIcons = state is DetailsUiState.Content)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (state) {
            DetailsUiState.Loading -> LoadingView(description = stringResource(R.string.details_loading))

            is DetailsUiState.Content -> RestaurantDetails(state.restaurant)

            DetailsUiState.NotFound -> EmptyView(
                title = stringResource(R.string.details_not_found_title),
                message = stringResource(R.string.details_not_found_message),
            )

            DetailsUiState.Failed -> ErrorView(
                title = stringResource(R.string.details_failed_title),
                message = stringResource(R.string.details_failed_message),
                retryLabel = stringResource(R.string.details_retry),
                onRetry = onRetry,
            )
        }
        if (state is DetailsUiState.Content) {
            val favorite = state.restaurant.isFavorite
            FavoriteButton(
                isFavorite = favorite,
                onClick = onFavoriteClick,
                contentDescription = stringResource(
                    if (favorite) R.string.details_favorite_remove else R.string.details_favorite_add,
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(MrDSpacing.sm),
            )
        }
        BackButton(
            contentDescription = stringResource(R.string.details_back),
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(MrDSpacing.sm),
        )
    }
}

@Composable
private fun RestaurantDetails(restaurant: RestaurantDetailsUi) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // No top padding on purpose: the hero draws behind the status bar.
        Box(modifier = Modifier.fillMaxWidth().height(HeroHeight)) {
            MrDImage(
                url = restaurant.imageUrl,
                dimmed = !restaurant.isOpen,
                modifier = Modifier.fillMaxSize(),
            )
            Box(modifier = Modifier.fillMaxSize().background(HeroScrim))
        }
        InfoCard(
            restaurant = restaurant,
            modifier = Modifier
                .offset(y = -CardOverlap)
                .padding(horizontal = MrDSpacing.md),
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InfoCard(restaurant: RestaurantDetailsUi, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(MrDSpacing.border, MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(
            modifier = Modifier.padding(MrDSpacing.md),
            verticalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
        ) {
            Text(text = restaurant.name, style = MaterialTheme.typography.headlineMedium)
            if (restaurant.cuisines.isNotEmpty()) {
                Text(
                    text = restaurant.cuisines,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(MrDSpacing.sm),
            ) {
                StatusBadge(
                    text = stringResource(if (restaurant.isOpen) R.string.details_open else R.string.details_closed),
                    isOpen = restaurant.isOpen,
                )
                restaurant.ratingText?.let { RatingPill(text = it) }
                restaurant.etaMinutes?.let { InfoChip(text = stringResource(R.string.details_eta, it)) }
                FeeChip(restaurant.deliveryFee)
            }
        }
    }
}

@Composable
private fun FeeChip(fee: DeliveryFee) {
    when (fee) {
        DeliveryFee.Unknown -> Unit
        DeliveryFee.Free -> InfoChip(text = stringResource(R.string.details_fee_free))
        is DeliveryFee.Paid -> InfoChip(text = stringResource(R.string.details_fee_paid, fee.formatted))
    }
}

private fun previewRestaurant(isOpen: Boolean = true) = RestaurantDetailsUi(
    name = "Cape Town Grill",
    cuisines = "Steakhouse · Grill",
    ratingText = "4.6",
    etaMinutes = 32,
    deliveryFee = DeliveryFee.Paid("R15.00"),
    isOpen = isOpen,
    imageUrl = null,
    isFavorite = false,
)

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    MrDTheme {
        DetailsScreenContent(
            state = DetailsUiState.Content(previewRestaurant()),
            onBack = {},
            onRetry = {},
            onFavoriteClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotFoundPreview() {
    MrDTheme {
        DetailsScreenContent(state = DetailsUiState.NotFound, onBack = {}, onRetry = {}, onFavoriteClick = {})
    }
}

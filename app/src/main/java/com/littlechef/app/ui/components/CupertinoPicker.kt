package com.littlechef.app.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * iOS-style barrel/wheel picker with infinite scroll, snap-to-center, and haptic feedback.
 *
 * Haptic fires only when scroll settles on a different item — no buzzing during scroll.
 * All item selection callbacks fire regardless; haptic is managed internally.
 */
@OptIn(FlowPreview::class)
@Composable
internal fun <T> CupertinoPicker(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    itemLabel: @Composable (T?) -> String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = false,
    getIcon: ((T?) -> Int)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val itemHeightDp = 40.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeightDp.toPx() }
    val verticalPaddingDp = 36.dp

    // Infinite list config
    val repeatCount = 1000
    val totalItems = items.size * repeatCount
    val middleStart = (repeatCount / 2) * items.size

    // Track last item that triggered haptic — prevents double-fire on snap-then-settle
    var lastHapticItem by remember { mutableStateOf<Any?>(null) }

    // Track snap state to avoid re-entering during our own animation
    var isSnapping by remember { mutableStateOf(false) }

    // Scroll to externally-changed selected item
    LaunchedEffect(selectedItem) {
        val targetIndex = middleStart + (items.indexOf(selectedItem).takeIf { it >= 0 } ?: 0)
        listState.scrollToItem(targetIndex)
    }

    // Auto-select during scroll: updates state for reactivity, NO haptic
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .debounce(100)
            .collect { (firstIndex, offset) ->
                val centeredIndex = if (offset.toFloat() < itemHeightPx / 2) firstIndex else firstIndex + 1
                val actualIndex = centeredIndex % items.size
                val actualItem = items[actualIndex]

                if (actualItem != selectedItem) {
                    onItemSelected(actualItem)
                }
            }
    }

    // Snap to center when scroll stops + fire haptic only on real item change
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && !isSnapping) {
            val firstIndex = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val targetIndex = if (offset < itemHeightPx / 2) firstIndex else firstIndex + 1

            // Determine settled item after snap
            val actualIndex = targetIndex % items.size
            val settledItem = items[actualIndex]

            // Update selection if scroll settled on a different item
            if (settledItem != selectedItem) {
                onItemSelected(settledItem)
            }

            // Haptic only when final selection differs from last haptic'd item
            if (settledItem != lastHapticItem) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                lastHapticItem = settledItem
            }

            // Smooth snap to center (async — won't block haptic firing)
            if (offset != 0 || targetIndex != firstIndex) {
                isSnapping = true
                coroutineScope.launch {
                    listState.animateScrollToItem(targetIndex)
                    isSnapping = false
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        // Selection highlight in the center
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .height(itemHeightDp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ) {}

        // Fade gradients at top and bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp)
                .align(Alignment.TopCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp)
                .align(Alignment.BottomCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        // Scrollable list with infinite items
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = verticalPaddingDp),
            horizontalAlignment = Alignment.CenterHorizontally,
            userScrollEnabled = true
        ) {
            items(totalItems) { index ->
                val actualIndex = index % items.size
                val item = items[actualIndex]
                val isSelected = item == selectedItem

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeightDp)
                        .clickable {
                            onItemSelected(item)
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 8.dp)
                    ) {
                        if (showIcon && getIcon != null) {
                            val iconRes = getIcon(item)
                            if (iconRes != 0) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                        }

                        Text(
                            text = itemLabel(item),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

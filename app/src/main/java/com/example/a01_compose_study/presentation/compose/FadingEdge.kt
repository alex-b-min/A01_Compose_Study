package com.ftd.ivi.cerence.ui.compose

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

//https://gist.github.com/dovahkiin98/85acb72ab0c4ddfc6b53413c955bcd10
fun Modifier.verticalFadingEdge(
    lazyListState: LazyListState,
    length: Dp,
    edgeColor: Color? = null,
) = composed(
    debugInspectorInfo {
        name = "length"
        value = length
    }
) {
    val color = edgeColor ?: MaterialTheme.colorScheme.surface

    drawWithContent {
        val topFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val firstItem = visibleItemsInfo.first()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    firstItem.index > 0 -> 1f // Added
                    firstItem.offset == viewportStartOffset -> 0f
                    firstItem.offset < viewportStartOffset -> firstItem.run {
                        abs(offset) / size.toFloat()
                    }

                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }
        val bottomFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val lastItem = visibleItemsInfo.last()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    lastItem.index < totalItemsCount - 1 -> 1f // Added
                    lastItem.offset + lastItem.size <= viewportEndOffset -> 0f // added the <=
                    lastItem.offset + lastItem.size > viewportEndOffset -> lastItem.run {
                        (size - (viewportEndOffset - offset)) / size.toFloat()  // Fixed the percentage computation
                    }

                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }

        drawContent()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
                startY = 0f,
                endY = topFadingEdgeStrength,
            ),
            size = Size(
                this.size.width,
                topFadingEdgeStrength
            ),
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startY = size.height - bottomFadingEdgeStrength,
                endY = size.height,
            ),
            topLeft = Offset(x = 0f, y = size.height - bottomFadingEdgeStrength),
        )
    }
}

fun Modifier.verticalBottomFadingEdge(
    lazyListState: LazyListState,
    length: Dp,
    edgeColor: Color? = null,
    fixStrength: Boolean = false
) = composed(
    debugInspectorInfo {
        name = "length"
        value = length
    }
) {
    val color = edgeColor ?: MaterialTheme.colorScheme.surface

    drawWithContent {
        //val bottomFadingEdgeStrength = length.value

        val bottomFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val lastItem = visibleItemsInfo.lastOrNull() ?: return@derivedStateOf 0f
                if (fixStrength) {
                    1f
                } else {
                    when {
                        visibleItemsInfo.size in 0..1 -> 0f
                        lastItem.index < totalItemsCount - 1 -> 1f // Added
                        lastItem.offset + lastItem.size <= viewportEndOffset -> 0f // added the <=
                        lastItem.offset + lastItem.size > viewportEndOffset -> lastItem.run {
                            (size - (viewportEndOffset - offset)) / size.toFloat()  // Fixed the percentage computation
                        }

                        else -> 1f
                    }
                }
            }.coerceAtMost(1f) * length.value

        }

        drawContent()
        //CustomLogger.e("makeBitmap FadingEdge height:${size.height} strength:${bottomFadingEdgeStrength}")
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startY = size.height - bottomFadingEdgeStrength,
                endY = size.height,
            ),
            topLeft = Offset(x = 0f, y = size.height - bottomFadingEdgeStrength),
        )
    }
}
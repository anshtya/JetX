package com.anshtya.jetx.common.ui.message

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

@Composable
fun MessageItemContent(
    message: String,
    modifier: Modifier = Modifier,
    details: @Composable () -> Unit
) {
    val itemProperties = remember { MessageItemProperties() }

    MessageLayout(
        itemProperties = itemProperties,
        modifier = modifier
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            onTextLayout = { textLayoutResult ->
                itemProperties.textLineCount = textLayoutResult.lineCount
                itemProperties.textWidth = textLayoutResult.size.width
                itemProperties.lastLineWidth =
                    textLayoutResult.getLineRight(textLayoutResult.lineCount - 1)
            }
        )
        details()
    }
}

@Composable
private fun MessageLayout(
    itemProperties: MessageItemProperties,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map {
                it.measure(Constraints(0, constraints.maxWidth))
            }

            val message = placeables[0]
            val details = placeables[1]

            if (itemProperties.textLineCount > 1 && itemProperties.lastLineWidth + details.measuredWidth >= itemProperties.textWidth) {
                itemProperties.itemWidth = message.measuredWidth
                itemProperties.itemHeight = message.measuredHeight + details.measuredHeight
            } else if (itemProperties.textLineCount > 1 && itemProperties.lastLineWidth + details.measuredWidth < itemProperties.textWidth) {
                itemProperties.itemWidth = message.measuredWidth
                itemProperties.itemHeight = message.measuredHeight
            } else if (itemProperties.textLineCount == 1 && message.measuredWidth + details.measuredWidth >= constraints.maxWidth) {
                itemProperties.itemWidth = message.measuredWidth
                itemProperties.itemHeight = message.measuredHeight + details.measuredHeight
            } else {
                itemProperties.itemWidth = message.measuredWidth + details.measuredWidth
                itemProperties.itemHeight = message.measuredHeight
                // Add more horizontal spacing between message and time
                itemProperties.itemWidth += 12
            }

            // Add more vertical spacing above time
            itemProperties.itemHeight += 5

            layout(
                width = itemProperties.itemWidth,
                height = itemProperties.itemHeight
            ) {
                message.placeRelative(0, 0)
                details.placeRelative(
                    x = itemProperties.itemWidth - details.width,
                    y = itemProperties.itemHeight - details.height
                )
            }
        }
    )
}
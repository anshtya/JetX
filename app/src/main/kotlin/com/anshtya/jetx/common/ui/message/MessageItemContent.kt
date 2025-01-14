package com.anshtya.jetx.common.ui.message

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

@Composable
fun MessageItemContent(
    message: String,
    time: String,
    modifier: Modifier = Modifier
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

        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
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
            val time = placeables[1]

            if (itemProperties.textLineCount > 1 && itemProperties.lastLineWidth + time.measuredWidth >= itemProperties.textWidth) {
                itemProperties.itemWidth = message.measuredWidth
                itemProperties.itemHeight = message.measuredHeight + time.measuredHeight
            } else if (itemProperties.textLineCount > 1 && itemProperties.lastLineWidth + time.measuredWidth < itemProperties.textWidth) {
                itemProperties.itemWidth = message.measuredWidth
                itemProperties.itemHeight = message.measuredHeight
            } else if (itemProperties.textLineCount == 1 && message.measuredWidth + time.measuredWidth >= constraints.maxWidth) {
                itemProperties.itemWidth = message.measuredWidth
                itemProperties.itemHeight = message.measuredHeight + time.measuredHeight
            } else {
                itemProperties.itemWidth = message.measuredWidth + time.measuredWidth
                itemProperties.itemHeight = message.measuredHeight
                // Add more horizontal spacing between message and time
                itemProperties.itemWidth += 10
            }

            // Add more vertical spacing above time
            itemProperties.itemHeight += 5

            layout(
                width = itemProperties.itemWidth,
                height = itemProperties.itemHeight
            ) {
                message.placeRelative(0, 0)
                time.placeRelative(
                    x = itemProperties.itemWidth - time.width,
                    y = itemProperties.itemHeight - time.height
                )
            }
        }
    )
}
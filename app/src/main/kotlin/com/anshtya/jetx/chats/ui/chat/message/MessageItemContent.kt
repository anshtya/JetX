package com.anshtya.jetx.chats.ui.chat.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshtya.jetx.database.model.AttachmentInfo
import com.anshtya.jetx.database.model.MessageStatus

private const val TEXT_LAYOUT_ID = 1
private const val DETAILS_LAYOUT_ID = 2

@Composable
fun MessageItemContent(
    text: String?,
    time: String,
    status: MessageStatus?,
    attachmentInfo: AttachmentInfo?,
    onAttachmentClick: (String) -> Unit,
    onAttachmentDownloadClick: (Int) -> Unit,
    onCancelDownloadClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemProperties = remember { MessageItemProperties() }

    Column(modifier) {
        attachmentInfo?.let {
            MessageAttachmentItem(
                attachmentInfo = attachmentInfo,
                onClick = onAttachmentClick,
                onDownloadClick = onAttachmentDownloadClick,
                onCancelDownloadClick = onCancelDownloadClick,
                modifier = Modifier
                    .sizeIn(maxWidth = 250.dp, maxHeight = 250.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.height(4.dp))
        }
        MessageLayout(
            attachmentExists = attachmentInfo != null,
            itemProperties = itemProperties,
            modifier = Modifier.widthIn(max = 250.dp)
        ) {
            Text(
                text = text ?: "",
                fontSize = 16.sp,
                onTextLayout = { textLayoutResult ->
                    itemProperties.textLineCount = textLayoutResult.lineCount
                    itemProperties.textWidth = textLayoutResult.size.width
                    itemProperties.lastTextLineWidth =
                        textLayoutResult.getLineRight(textLayoutResult.lineCount - 1)
                },
                modifier = Modifier.layoutId(TEXT_LAYOUT_ID)
            )
            MessageDetails(
                time = time,
                status = status,
                modifier = Modifier.layoutId(DETAILS_LAYOUT_ID)
            )
        }
    }
}

@Composable
private fun MessageLayout(
    attachmentExists: Boolean,
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

            val text = placeables[0]
            val details = placeables[1]

            if (itemProperties.textLineCount > 1 && itemProperties.lastTextLineWidth + details.measuredWidth >= itemProperties.textWidth) {
                itemProperties.itemWidth = text.measuredWidth
                itemProperties.itemHeight = text.measuredHeight + details.measuredHeight
            } else if (itemProperties.textLineCount > 1 && itemProperties.lastTextLineWidth + details.measuredWidth < itemProperties.textWidth) {
                itemProperties.itemWidth = text.measuredWidth
                itemProperties.itemHeight = text.measuredHeight
            } else if (itemProperties.textLineCount == 1 && text.measuredWidth + details.measuredWidth >= constraints.maxWidth) {
                itemProperties.itemWidth = text.measuredWidth
                itemProperties.itemHeight = text.measuredHeight + details.measuredHeight
            } else {
                itemProperties.itemWidth = text.measuredWidth + details.measuredWidth
                itemProperties.itemHeight = text.measuredHeight
                // Add more horizontal spacing between message and time
                itemProperties.itemWidth += 12
            }

            // If attachment exists, add remaining space of constraints to item width
            if (attachmentExists) {
                itemProperties.itemWidth += (constraints.maxWidth - itemProperties.itemWidth)
            }

            // Add more vertical spacing above time
            itemProperties.itemHeight += 5

            layout(
                width = itemProperties.itemWidth,
                height = itemProperties.itemHeight
            ) {
                text.placeRelative(0, 0)
                details.placeRelative(
                    x = itemProperties.itemWidth - details.width,
                    y = itemProperties.itemHeight - details.height
                )
            }
        }
    )
}
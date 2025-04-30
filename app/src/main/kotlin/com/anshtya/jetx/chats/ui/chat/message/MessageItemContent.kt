package com.anshtya.jetx.chats.ui.chat.message

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.model.AttachmentInfo

private const val ATTACHMENT_LAYOUT_ID = 1
private const val TEXT_LAYOUT_ID = 2
private const val DETAILS_LAYOUT_ID = 3

@Composable
fun MessageItemContent(
    text: String?,
    time: String,
    status: MessageStatus?,
    attachmentInfo: AttachmentInfo?,
    onAttachmentDownloadClick: (Int) -> Unit,
    onCancelDownloadClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemProperties = remember { MessageItemProperties() }

    MessageLayout(
        itemProperties = itemProperties,
        modifier = modifier
            .sizeIn(
                maxWidth = attachmentInfo?.width?.let {
                    with(LocalDensity.current) { it.toDp().coerceAtMost(250.dp) }
                } ?: 250.dp
            )
    ) {
        attachmentInfo?.let {
            MessageAttachmentItem(
                attachmentInfo = attachmentInfo,
                onClick = {}, //TODO: implement media view
                onDownloadClick = onAttachmentDownloadClick,
                onCancelDownloadClick = onCancelDownloadClick,
                modifier = Modifier
                    .layoutId(ATTACHMENT_LAYOUT_ID)
                    .fillMaxWidth()
                    .heightIn(
                        max = attachmentInfo.height?.let {
                            with(LocalDensity.current) { it.toDp().coerceAtMost(250.dp) }
                        } ?: 250.dp,
                        min = 56.dp
                    )
            )
        }
        Text(
            text = text ?: "",
            style = MaterialTheme.typography.bodyMedium,
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
            val attachment = measurables.find { it.layoutId == ATTACHMENT_LAYOUT_ID }
                ?.measure(Constraints(0, constraints.maxWidth))
            val text = measurables.find { it.layoutId == TEXT_LAYOUT_ID }
                ?.measure(Constraints(0, constraints.maxWidth))
            val details = measurables.find { it.layoutId == DETAILS_LAYOUT_ID }
                ?.measure(Constraints(0, constraints.maxWidth))!!

            if (attachment != null) {
                itemProperties.itemWidth = attachment.measuredWidth
                itemProperties.itemHeight = attachment.measuredHeight
            }
            // till here itemHeight and itemWidth are non zero if attachment is not null

            if (text != null) {
                if (itemProperties.textLineCount > 1 &&
                    itemProperties.lastTextLineWidth + details.measuredWidth >= itemProperties.textWidth
                ) {
                    if (itemProperties.itemWidth == 0)
                        itemProperties.itemWidth = text.measuredWidth

                    if (itemProperties.itemHeight == 0)
                        itemProperties.itemHeight = text.measuredHeight + details.measuredHeight
                    else
                        itemProperties.itemHeight += text.measuredHeight + details.measuredHeight

                } else if (itemProperties.textLineCount > 1 &&
                    itemProperties.lastTextLineWidth + details.measuredWidth < itemProperties.textWidth
                ) {
                    if (itemProperties.itemWidth == 0)
                        itemProperties.itemWidth = text.measuredWidth

                    if (itemProperties.itemHeight == 0)
                        itemProperties.itemHeight = text.measuredHeight
                    else
                        itemProperties.itemHeight += text.measuredHeight

                } else if (itemProperties.textLineCount == 1 &&
                    text.measuredWidth + details.measuredWidth >= constraints.maxWidth
                ) {
                    if (itemProperties.itemWidth == 0)
                        itemProperties.itemWidth = text.measuredWidth

                    if (itemProperties.itemHeight == 0)
                        itemProperties.itemHeight = text.measuredHeight + details.measuredHeight
                    else
                        itemProperties.itemHeight += text.measuredHeight + details.measuredHeight

                } else {
                    if (itemProperties.itemWidth == 0) {
                        itemProperties.itemWidth = text.measuredWidth + details.measuredWidth
                        // Add more horizontal spacing between message and time
                        itemProperties.itemWidth += 12
                    }

                    if (itemProperties.itemHeight == 0)
                        itemProperties.itemHeight = text.measuredHeight
                    else
                        itemProperties.itemHeight += text.measuredHeight
                }
            }

            // Add more vertical spacing above time
            itemProperties.itemHeight += 5

            layout(
                width = itemProperties.itemWidth,
                height = itemProperties.itemHeight
            ) {
                attachment?.placeRelative(0,0)
                text?.placeRelative(0, 0)
                details.placeRelative(
                    x = itemProperties.itemWidth - details.width,
                    y = itemProperties.itemHeight - details.height
                )
            }
        }
    )
}
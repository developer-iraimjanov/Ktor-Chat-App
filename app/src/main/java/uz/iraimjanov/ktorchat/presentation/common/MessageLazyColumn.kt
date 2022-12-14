package uz.iraimjanov.ktorchat.presentation.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.iraimjanov.ktorchat.data.remote.dto.MessageDto
import uz.iraimjanov.ktorchat.presentation.chat.ChatState
import uz.iraimjanov.ktorchat.ui.theme.Dark
import uz.iraimjanov.ktorchat.ui.theme.Light
import uz.iraimjanov.ktorchat.ui.theme.brush

@ExperimentalFoundationApi
@Composable
fun MessageLazyColumn(
    modifier: Modifier,
    username: String,
    state: ChatState,
    openBottomSheet: (MessageDto) -> Unit,
    copyText: (String) -> Unit
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp),
        reverseLayout = true,
    ) {
        items(state.messages) { message ->
            MessageItem(
                message = message,
                username = username,
                openBottomSheet = { openBottomSheet(it) },
                copyText = { copyText(it) }
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun MessageItem(
    message: MessageDto,
    username: String,
    openBottomSheet: (MessageDto) -> Unit,
    copyText: (String) -> Unit
) {
    val myMessage = message.username == username
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (myMessage) {
                Spacer(modifier = Modifier.weight(1f))
            }
            Box(
                modifier = Modifier.weight(4f),
                contentAlignment = if (myMessage) {
                    Alignment.CenterEnd
                } else {
                    Alignment.CenterStart
                }
            ) {
                Column(
                    modifier = if (myMessage) {
                        Modifier
                            .defaultMinSize(minWidth = 150.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = 12.dp
                                )
                            )
                            .background(
                                brush = brush,
                            )
                            .combinedClickable(onClick = {}, onLongClick = {
                                openBottomSheet(message)
                            })
                            .padding(8.dp)
                    } else {
                        Modifier
                            .defaultMinSize(minWidth = 150.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomEnd = 12.dp
                                )
                            )
                            .background(
                                color = Light,
                            )
                            .combinedClickable(onClick = {}, onLongClick = {
                                copyText(message.text)
                            })
                            .padding(8.dp)
                    },
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = message.username,
                        fontWeight = FontWeight.SemiBold,
                        color = if (myMessage) Light else Dark,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Text(
                        text = message.text,
                        color = if (myMessage) Light else Dark,
                    )
                    Text(
                        text = message.formattedTime(),
                        color = if (myMessage) Light else Dark,
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
            if (!myMessage) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
    }

}
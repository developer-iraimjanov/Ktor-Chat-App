package uz.iraimjanov.ktorchat.presentation.chat

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.iraimjanov.ktorchat.R
import uz.iraimjanov.ktorchat.data.remote.dto.MessageDto
import uz.iraimjanov.ktorchat.presentation.common.MessageLazyColumn
import uz.iraimjanov.ktorchat.ui.theme.*
import uz.iraimjanov.ktorchat.util.ScreenSetting
import uz.iraimjanov.ktorchat.util.copyToClipboard
import uz.iraimjanov.ktorchat.util.noRippleClickable

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun ChatScreen(
    username: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messageText by viewModel.messageText
    val edc by viewModel.edc
    val editMode by viewModel.editMode
    val message by viewModel.message
    val isDDShowing by viewModel.isDDShowing
    val systemUiController = rememberSystemUiController()
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = viewModel.state.value

    if (isSystemInDarkTheme()) {
        systemUiController.setSystemBarsColor(
            color = Dark
        )
    } else {
        systemUiController.setSystemBarsColor(
            color = Light
        )
    }

    ScreenSetting(boolean = true)

    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler {
        if (edc) {
            viewModel.onEDCChange(false)
        } else {
            (context as Activity).finish()
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.connectToChat()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (isDDShowing) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { viewModel.onDDChange(false) },
            title = { Text(text = "Delete message") },
            text = {
                Text(
                    text = "Are you sure you want to delete?",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            },
            shape = RoundedCornerShape(8.dp),
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {
                Text(
                    text = "Delete",
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .noRippleClickable {
                            viewModel.deleteMessage(id = message.id)
                            viewModel.onDDChange(false)
                        },
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            },
            dismissButton = {
                Text(
                    text = "Cancel",
                    color = Blue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .noRippleClickable {
                            viewModel.onDDChange(false)
                        },
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        )
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (app_bar, lazy, snack_bar, chat_bar, image_background, background_bottom_sheet) = createRefs()

        AppBar(modifier = Modifier.constrainAs(app_bar) {
            width = Dimension.fillToConstraints
            height = Dimension.value(56.dp)
            linkTo(start = parent.start, end = parent.end)
            top.linkTo(anchor = parent.top)
        })

        Image(
            modifier = Modifier.constrainAs(image_background) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
                linkTo(start = parent.start, end = parent.end)
                linkTo(top = app_bar.bottom, bottom = chat_bar.top)
            },
            painter = painterResource(id = R.drawable.chat_background),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )

        MessageLazyColumn(modifier = Modifier
            .constrainAs(lazy) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
                linkTo(start = parent.start, end = parent.end)
                linkTo(top = app_bar.bottom, bottom = chat_bar.top)
            },
            username = username,
            state = state,
            openBottomSheet = {
                viewModel.onEDCChange(true)
                viewModel.onClassMessageChange(it)
            },
            copyText = {
                context.copyToClipboard(it)
                coroutineScope.launch {
                    snackState.showSnackbar("")
                }
            }
        )

        if (edc) {
            Box(modifier = Modifier
                .constrainAs(background_bottom_sheet) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints

                    linkTo(top = app_bar.bottom, bottom = chat_bar.top)
                    linkTo(start = parent.start, end = parent.end)
                }
                .background(Color.Black.copy(0.2f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.onEDCChange(false)
                })
        }

        SnackbarHost(hostState = snackState, modifier = Modifier.constrainAs(snack_bar) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent

            linkTo(start = parent.start, end = parent.end)
            bottom.linkTo(anchor = chat_bar.top)
        }) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(0.8f)),
            ) {
                val (icon, text) = createRefs()
                Icon(
                    modifier = Modifier.constrainAs(icon) {
                        width = Dimension.value(24.dp)
                        height = Dimension.value(24.dp)

                        linkTo(
                            top = parent.top,
                            bottom = parent.bottom,
                            topMargin = 12.dp,
                            bottomMargin = 12.dp
                        )
                        start.linkTo(anchor = parent.start, margin = 16.dp)
                    },
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "Copy",
                    tint = Light
                )
                Text(
                    modifier = Modifier.constrainAs(text) {
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent

                        linkTo(top = parent.top, bottom = parent.bottom)
                        linkTo(
                            start = icon.end,
                            startMargin = 8.dp,
                            end = parent.end,
                            endMargin = 16.dp
                        )
                    },
                    text = stringResource(id = R.string.text_copy_description),
                    color = Light,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                )
            }
        }

        ChatBar(
            modifier = Modifier
                .defaultMinSize(minHeight = 56.dp)
                .constrainAs(chat_bar) {
                    width = Dimension.fillToConstraints
                    linkTo(start = parent.start, end = parent.end)
                    bottom.linkTo(anchor = parent.bottom)
                }
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            messageText = messageText,
            message = message,
            edc = edc,
            editMode = editMode,
            onMessageChange = { viewModel.onMessageChange(it) },
            onSendMessage = {
                if (editMode) {
                    val editedMessage = message.copy(text = messageText)
                    viewModel.updateMessage(editedMessage)
                    viewModel.onEditModeChange(false)
                    keyboardController!!.hide()
                } else {
                    viewModel.sendMessage()
                }
            },
            onCopyMessage = {
                context.copyToClipboard(message.text)
                viewModel.onEDCChange(false)
                coroutineScope.launch {
                    snackState.showSnackbar("")
                }
            },
            onDeleteMessage = {
                viewModel.onEDCChange(false)
                viewModel.onDDChange(true)
            },
            onEditMessage = {
                viewModel.onEDCChange(false)
                viewModel.onEditModeChange(true)
                viewModel.onMessageChange(message.text)
            },
            onEditModeChange = {
                viewModel.onEditModeChange(it)
                viewModel.onMessageChange("")
            }
        )
    }
}

@Composable
fun AppBar(modifier: Modifier) {
    Surface(
        modifier = modifier,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = stringResource(id = R.string.groupChat),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = if (isSystemInDarkTheme()) Light else Dark
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ChatBar(
    modifier: Modifier,
    messageText: String,
    message: MessageDto,
    edc: Boolean,
    editMode: Boolean,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onCopyMessage: () -> Unit,
    onDeleteMessage: () -> Unit,
    onEditMessage: () -> Unit,
    onEditModeChange: (Boolean) -> Unit,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (edc) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            onEditMessage()
                        },
                    text = "Edit",
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            onDeleteMessage()
                        },
                    text = "Delete",
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            onCopyMessage()
                        },
                    text = "Copy",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column {
                if (editMode) {
                    Surface(shadowElevation = 1.dp, color = MaterialTheme.colorScheme.background) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { }, enabled = false) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = "Edit",
                                    tint = Blue
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Edit message", color = Blue)
                                Text(
                                    text = message.text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                            IconButton(onClick = { onEditModeChange(false) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cross),
                                    contentDescription = "Cross",
                                    tint = Gray30
                                )
                            }
                        }
                    }
                }
                ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                    val (edt, btn) = createRefs()
                    TextField(
                        modifier = Modifier.constrainAs(edt) {
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                            linkTo(top = parent.top, bottom = parent.bottom)
                            linkTo(
                                start = parent.start,
                                end = btn.start,
                            )
                        },
                        value = messageText,
                        onValueChange = { onMessageChange(it) },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.message),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                color = Gray30
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            containerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.LightGray,
                            selectionColors = TextSelectionColors(
                                handleColor = Color.LightGray.copy(0.9f),
                                backgroundColor = Color.LightGray.copy(0.7f)
                            )
                        ),
                        maxLines = 5,
                    )

                    Box(
                        modifier = Modifier
                            .constrainAs(btn) {
                                width = Dimension.value(40.dp)
                                height = Dimension.value(40.dp)

                                end.linkTo(anchor = parent.end, margin = 16.dp)
                                bottom.linkTo(anchor = parent.bottom, margin = 8.dp)

                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = if (isSystemInDarkTheme()) Gray30 else Gray20)
                            .clickable(
                                indication = rememberRipple(color = Dark),
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onSendMessage()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp),
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = "Send",
                        )
                    }
                }
            }
        }
    }
}
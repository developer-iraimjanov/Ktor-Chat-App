package uz.iraimjanov.ktorchat.presentation.username

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import uz.iraimjanov.ktorchat.R
import uz.iraimjanov.ktorchat.navigation.Screen
import uz.iraimjanov.ktorchat.ui.theme.Dark
import uz.iraimjanov.ktorchat.ui.theme.Light
import uz.iraimjanov.ktorchat.ui.theme.brush
import uz.iraimjanov.ktorchat.util.ScreenSetting


@ExperimentalMaterial3Api
@ExperimentalTextApi
@Composable
fun UsernameScreen(
    navController: NavController,
    viewModel: UsernameViewModel = hiltViewModel(),
) {
    val usernameText by viewModel.usernameText
    val passwordText by viewModel.passwordText
    val passwordIsShowing by viewModel.passwordIsShowing
    val interactionSourceUsername = remember { MutableInteractionSource() }
    val isFocusedUsername by interactionSourceUsername.collectIsFocusedAsState()
    val interactionSourcePassword = remember { MutableInteractionSource() }
    val isFocusedPassword by interactionSourcePassword.collectIsFocusedAsState()
    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = Color.Black.copy(0.5f),
        darkIcons = false,
    )
    if (isSystemInDarkTheme()) {
        systemUiController.setNavigationBarColor(
            color = Dark
        )
    } else {
        systemUiController.setNavigationBarColor(
            color = Light
        )
    }

    ScreenSetting(boolean = false)

    LaunchedEffect(key1 = true) {
        viewModel.onJoinChat.collectLatest {
            navController.popBackStack()
            navController.navigate(Screen.Chat.passUsername(username = usernameText))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.image_login_background),
                contentDescription = "Image",
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color.Transparent)
                    .clip(RoundedCornerShape(topStart = 40.dp))
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f),
        ) {
            val (tv_login, edt_username, edt_password, btn_login) = createRefs()

            Text(
                modifier = Modifier.constrainAs(tv_login) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp
                    )
                    top.linkTo(anchor = parent.top)
                },
                text = stringResource(id = R.string.login),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    brush = brush
                )
            )

            UsernameTextField(
                modifier = if (isFocusedUsername) {
                    Modifier
                        .constrainAs(edt_username) {
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                startMargin = 16.dp,
                                endMargin = 16.dp
                            )
                            top.linkTo(tv_login.bottom, margin = 40.dp)
                        }
                        .border(
                            width = 2.dp,
                            brush = brush,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 4.dp)
                } else {
                    Modifier
                        .constrainAs(edt_username) {
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                startMargin = 16.dp,
                                endMargin = 16.dp
                            )
                            top.linkTo(tv_login.bottom, margin = 40.dp)
                        }
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 4.dp)
                },
                usernameText = usernameText,
                interactionSource = interactionSourceUsername,
                onTextChanged = { viewModel.onUsernameTextChange(it) }
            )

            PasswordTextField(
                modifier = if (isFocusedPassword) {
                    Modifier
                        .constrainAs(edt_password) {
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                startMargin = 16.dp,
                                endMargin = 16.dp
                            )
                            top.linkTo(edt_username.bottom, margin = 20.dp)
                        }
                        .border(
                            width = 2.dp,
                            brush = brush,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 4.dp)
                } else {
                    Modifier
                        .constrainAs(edt_password) {
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                            linkTo(
                                start = parent.start,
                                end = parent.end,
                                startMargin = 16.dp,
                                endMargin = 16.dp
                            )
                            top.linkTo(edt_username.bottom, margin = 20.dp)
                        }
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 4.dp)
                },
                passwordText = passwordText,
                passwordIsShowing = passwordIsShowing,
                interactionSource = interactionSourcePassword,
                onTextChanged = { viewModel.onPasswordTextChange(it) },
                onEyeClicked = { viewModel.onEyeClicked(it) },
            )

            Box(
                modifier = Modifier
                    .constrainAs(btn_login) {
                        width = Dimension.fillToConstraints
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            startMargin = 16.dp,
                            endMargin = 16.dp
                        )
                        bottom.linkTo(anchor = parent.bottom, margin = 60.dp)
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush = brush)
                    .clickable {
                        viewModel.onJoinClick()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier
                        .padding(12.dp),
                    text = stringResource(id = R.string.login),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun UsernameTextField(
    modifier: Modifier,
    usernameText: String,
    interactionSource: MutableInteractionSource,
    onTextChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = usernameText, onValueChange = { onTextChanged(it.trim()) },
        singleLine = true,
        leadingIcon = {
            IconButton(onClick = {}, enabled = false) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Username",
                    tint = Color.Gray.copy(0.7f),
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(id = R.string.username),
                color = Color.Gray.copy(0.3f)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = if (isSystemInDarkTheme()) Dark else Light,
            textColor = Color.Black,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Gray.copy(0.7f)
        ),
        textStyle = TextStyle(
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
        ),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
    )
}

@ExperimentalMaterial3Api
@Composable
fun PasswordTextField(
    modifier: Modifier,
    passwordText: String,
    passwordIsShowing: Boolean,
    interactionSource: MutableInteractionSource,
    onTextChanged: (String) -> Unit,
    onEyeClicked: (Boolean) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = passwordText, onValueChange = { onTextChanged(it.trim()) },
        singleLine = true,
        leadingIcon = {
            IconButton(onClick = {}, enabled = false) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = "Username",
                    tint = Color.Gray.copy(0.7f),
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                onEyeClicked(!passwordIsShowing)
            }) {
                Icon(
                    painter = if (passwordIsShowing) painterResource(id = R.drawable.ic_eye_open) else painterResource(
                        id = R.drawable.ic_eye_closed
                    ),
                    contentDescription = "Eye",
                    tint = Color.Gray.copy(0.7f),
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(id = R.string.password),
                color = Color.Gray.copy(0.3f)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = if (isSystemInDarkTheme()) Dark else Light,
            textColor = Color.Black,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Gray.copy(0.7f),
        ),
        textStyle = TextStyle(
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
        ),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (passwordIsShowing) VisualTransformation.None
        else PasswordVisualTransformation(),
    )
}

@ExperimentalMaterial3Api
@ExperimentalTextApi
@Preview(showBackground = true)
@Composable
fun UsernameScreenPreview() {
    UsernameScreen(rememberNavController())
}
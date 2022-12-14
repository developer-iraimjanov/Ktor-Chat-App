package uz.iraimjanov.ktorchat.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.iraimjanov.ktorchat.presentation.chat.ChatScreen
import uz.iraimjanov.ktorchat.presentation.username.UsernameScreen

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalTextApi
@ExperimentalMaterial3Api
@Composable
fun SetupNavGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.Username.route) {
        composable(route = Screen.Username.route) {
            UsernameScreen(navHostController)
        }
        composable(route = Screen.Chat.route, arguments = listOf(
            navArgument(CHAT_ARGUMENT) {
                type = NavType.StringType
                nullable = true
            }
        )) {
            val username = it.arguments?.getString(CHAT_ARGUMENT) ?: ""
            ChatScreen(username = username)
        }
    }
}
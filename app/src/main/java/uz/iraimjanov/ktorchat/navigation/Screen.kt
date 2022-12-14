package uz.iraimjanov.ktorchat.navigation

const val CHAT_ARGUMENT = "username"

sealed class Screen(val route: String) {
    object Username : Screen("username_screen")
    object Chat : Screen("chat_screen/{$CHAT_ARGUMENT}") {
        fun passUsername(username: String): String {
            return "chat_screen/$username"
        }
    }
}
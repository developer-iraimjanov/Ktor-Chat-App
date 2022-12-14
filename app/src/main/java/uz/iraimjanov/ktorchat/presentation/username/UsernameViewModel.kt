package uz.iraimjanov.ktorchat.presentation.username

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor() : ViewModel() {

    private var _usernameText = mutableStateOf("")
    val usernameText: State<String> = _usernameText

    private var _passwordText = mutableStateOf("")
    val passwordText: State<String> = _passwordText

    private var _passwordIsShowing = mutableStateOf(false)
    val passwordIsShowing = _passwordIsShowing

    private var _onJoinChat = MutableSharedFlow<String>()
    val onJoinChat: SharedFlow<String> = _onJoinChat.asSharedFlow()

    fun onUsernameTextChange(text: String) {
        _usernameText.value = text
    }

    fun onPasswordTextChange(text: String) {
        _passwordText.value = text
    }

    fun onEyeClicked(boolean: Boolean) {
        _passwordIsShowing.value = boolean
    }

    fun onJoinClick() {
        viewModelScope.launch {
            if (usernameText.value.isNotEmpty() && passwordText.value.isNotEmpty()) {
                _onJoinChat.emit(usernameText.value)
            }
        }
    }
}
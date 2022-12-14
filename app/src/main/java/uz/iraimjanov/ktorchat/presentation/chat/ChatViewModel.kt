package uz.iraimjanov.ktorchat.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uz.iraimjanov.ktorchat.data.remote.ChatSocketService
import uz.iraimjanov.ktorchat.data.remote.MessageService
import uz.iraimjanov.ktorchat.data.remote.dto.MessageDto
import uz.iraimjanov.ktorchat.data.remote.dto.SocketModel
import uz.iraimjanov.ktorchat.util.Resource
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private var _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private var _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private var _message = mutableStateOf(MessageDto())
    val message: State<MessageDto> = _message

    private var _edc = mutableStateOf(false)
    val edc: State<Boolean> = _edc

    private var _editMode = mutableStateOf(false)
    val editMode: State<Boolean> = _editMode

    private var _isDDShowing = mutableStateOf(false)
    val isDDShowing: State<Boolean> = _isDDShowing

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun onEDCChange(value: Boolean) {
        _edc.value = value
    }

    fun onEditModeChange(value: Boolean) {
        _editMode.value = value
    }

    fun onDDChange(value: Boolean) {
        _isDDShowing.value = value
    }

    fun onClassMessageChange(message: MessageDto) {
        _message.value = message
    }

    fun connectToChat() {
        getAllMessages()
        savedStateHandle.get<String>("username")?.let { username ->
            viewModelScope.launch {
                val result = chatSocketService.initSession(username)
                when (result) {
                    is Resource.Success -> {
                        chatSocketService.observeMessages().onEach { socketModel ->
                            val newList = state.value.messages.toMutableList().apply {
                                when (socketModel.mode) {
                                    "add" -> {
                                        add(0, socketModel.message!!)
                                    }
                                    "edit" -> {
                                        val index =
                                            indexOfFirst { it.id == socketModel.message!!.id }
                                        this[index] = socketModel.message!!
                                    }
                                    "delete" -> {
                                        val index = indexOfFirst { it.id == socketModel.id }
                                        removeAt(index)
                                    }
                                }
                            }

                            _state.value = state.value.copy(
                                messages = newList
                            )
                        }.launchIn(viewModelScope)
                    }
                    is Resource.Error -> {
                        _toastEvent.emit(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    private fun getAllMessages() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val result = messageService.getAllMessages()
            _state.value = state.value.copy(
                messages = result,
                isLoading = false
            )
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotEmpty()) {
                val socketModel = SocketModel(mode = "add", messageText = messageText.value)
                val request = Json.encodeToString(socketModel)
                chatSocketService.sendMessage(request)
                _messageText.value = ""
            }
        }
    }

    fun updateMessage(messageDto: MessageDto) {
        viewModelScope.launch {
            if (messageText.value.isNotEmpty()) {
                val socketModel = SocketModel(mode = "edit", message = messageDto)
                val request = Json.encodeToString(socketModel)
                chatSocketService.sendMessage(request)
                _messageText.value = ""
            }
        }
    }

    fun deleteMessage(id: String) {
        viewModelScope.launch {
            val socketModel = SocketModel(mode = "delete", id = id)
            val request = Json.encodeToString(socketModel)
            chatSocketService.sendMessage(request)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
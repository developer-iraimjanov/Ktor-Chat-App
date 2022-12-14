package uz.iraimjanov.ktorchat.presentation.chat

import uz.iraimjanov.ktorchat.data.remote.dto.MessageDto


data class ChatState(
    val messages: List<MessageDto> = emptyList(),
    val isLoading: Boolean = false
)
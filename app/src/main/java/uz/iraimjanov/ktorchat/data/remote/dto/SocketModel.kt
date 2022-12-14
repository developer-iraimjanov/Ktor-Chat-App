package uz.iraimjanov.ktorchat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SocketModel(
    val mode: String,
    val messageText: String? = null,
    val message: MessageDto? = null,
    val id: String? = null,
)
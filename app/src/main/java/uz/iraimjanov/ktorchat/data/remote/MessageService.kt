package uz.iraimjanov.ktorchat.data.remote

import uz.iraimjanov.ktorchat.data.remote.dto.MessageDto

interface MessageService {
    suspend fun getAllMessages(): List<MessageDto>

    companion object {
        const val BASE_URL = "http://192.168.114.7:8080"
    }

    sealed class EndPoint(val url: String) {
        object GetAllMessages : EndPoint("$BASE_URL/message")
    }
}
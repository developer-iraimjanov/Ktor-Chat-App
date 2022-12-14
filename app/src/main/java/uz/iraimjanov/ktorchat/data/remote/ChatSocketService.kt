package uz.iraimjanov.ktorchat.data.remote

import kotlinx.coroutines.flow.Flow
import uz.iraimjanov.ktorchat.data.remote.dto.MessageDto
import uz.iraimjanov.ktorchat.data.remote.dto.SocketModel
import uz.iraimjanov.ktorchat.util.Resource

interface ChatSocketService {

    suspend fun initSession(
        username: String
    ): Resource<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<SocketModel>

    suspend fun closeSession()

    companion object {
        const val BASE_URL = "ws://192.168.114.7:8080"
    }

    sealed class EndPoint(val url: String) {
        object ChatSocket : EndPoint("$BASE_URL/chat-socket")
    }
}
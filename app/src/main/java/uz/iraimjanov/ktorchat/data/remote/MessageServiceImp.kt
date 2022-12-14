package uz.iraimjanov.ktorchat.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import uz.iraimjanov.ktorchat.data.remote.dto.MessageDto

class MessageServiceImp(
    private val client: HttpClient
) : MessageService {
    override suspend fun getAllMessages(): List<MessageDto> {
        return try {
            client.get(MessageService.EndPoint.GetAllMessages.url)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
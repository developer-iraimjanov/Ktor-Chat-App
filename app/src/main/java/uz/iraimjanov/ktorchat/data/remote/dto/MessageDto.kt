package uz.iraimjanov.ktorchat.data.remote.dto

import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.*

@Serializable
data class MessageDto(
    var text: String = "",
    var timestamp: Long = 0L,
    var username: String = "",
    var id: String = "",
) {
    fun formattedTime(): String {
        val date = Date(timestamp)
        return DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
    }
}
package http

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val BOT_TOKEN_SIMON = "7109385107:AAFMqCA1MjRqA-FScOkgu_0Id3PRSamuTps"
private const val CHAT_ID_SIMON = "6837926469"

private const val BOT_TOKEN_ALINA = "7973787835:AAE74QnGdjNOhLoq49pOXXxCCSRsZZ0qEOk"
private const val CHAT_ID_ALINA = "8004900544"

suspend fun notify(message: String) = coroutineScope {
    Logger.log("Sending notification: $message")
    launch {
        sendRequest(message, BOT_TOKEN_SIMON, CHAT_ID_SIMON)
        Logger.log("Notification sent to Simon.")
    }
    launch {
        sendRequest(message, BOT_TOKEN_ALINA, CHAT_ID_ALINA)
        Logger.log("Notification sent to Alina.")
    }
}

private suspend fun sendRequest(message: String, botToken: String, chatId: String) {
    httpClient.post("https://api.telegram.org/bot$botToken/sendMessage") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "chat_id" to chatId,
                "text" to message,
                "parse_mode" to "markdown",
                "disable_notification" to "false",
            )
        )
    }
}
package http

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

private val jsonModule = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    serializersModule = SerializersModule {
        contextual(Instant.serializer())
        contextual(LocalDate.serializer())
    }
}

val httpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(jsonModule)
    }
}
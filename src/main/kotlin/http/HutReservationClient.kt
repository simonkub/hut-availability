package http

import Availability
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Suppress("unused", "EnumEntryName")
@Serializable
private enum class HutAvailabilityPercentage {
    FULL, `NEARLY FULL`, AVAILABLE, CLOSED
}

@Suppress("unused")
@Serializable
private enum class HutStatus {
    SERVICED, CLOSED
}

// 499 = Matratzenlager
// 498 = Mehrbettzimmer
// 1832 = Doppelzimmer
// 4789 = Hundezimmer
private typealias BedsPerCategory = Map<String, Int>

@Serializable
private data class HutAvailabilityResponse(
    val freeBedsPerCategory: BedsPerCategory,
    val freeBeds: Int?,
    val hutStatus: HutStatus,
    val date: Instant,
    val dateFormatted: String,
    val totalSleepingPlaces: Int,
    val percentage: HutAvailabilityPercentage
) {
    fun toAvailability(): Availability? {
        val availableDoubleRoomBeds = freeBedsPerCategory["1832"] ?: return null
        val availableMattressRoomBeds = freeBedsPerCategory["499"] ?: return null
        val availableDormBeds = freeBedsPerCategory["498"] ?: return null

        return Availability(
            date = date.toLocalDateTime(TimeZone.of("Europe/Berlin")).date,
            availableDoubleRoomBeds = availableDoubleRoomBeds,
            availableMattressRoomBeds = availableMattressRoomBeds,
            availableDormBeds = availableDormBeds,
        )
    }
}

suspend fun getKaerlingerHausAvailabilities(): List<Availability>? = try {
    httpClient.get("https://www.hut-reservation.org/api/v1/reservation/getHutAvailability?hutId=195&step=WIZARD")
        .body<List<HutAvailabilityResponse>>()
        .filter { it.hutStatus == HutStatus.SERVICED }
        .mapNotNull(HutAvailabilityResponse::toAvailability)
} catch (e: Exception) {
    println("Error: ${e.message}")
    null
}
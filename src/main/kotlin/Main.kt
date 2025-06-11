import http.getKaerlingerHausAvailabilities
import http.httpClient
import http.notify
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime

data class Availability(
    val date: LocalDate,
    val availableDoubleRoomBeds: Int,
    val availableMattressRoomBeds: Int,
    val availableDormBeds: Int,
) {
    fun format(): String = buildString {
        appendLine("**${date.format()}:**")
        appendIfAvailable(availableDoubleRoomBeds, "Doppelzimmer")
        appendIfAvailable(availableMattressRoomBeds, "Matratzenlager")
        appendIfAvailable(availableDormBeds, "Mehrbettzimmer")
    }

    private fun StringBuilder.appendIfAvailable(count: Int, description: String) {
        if (count >= 2) appendLine("- $count Schlafplätze im $description frei")
    }
}

val preferredDates: List<ClosedRange<LocalDate>> = listOf(
    LocalDate(2025, 7, 8)..LocalDate(2025, 7, 24),
    LocalDate(2025, 7, 28)..LocalDate(2025, 8, 7),
    LocalDate(2025, 8, 11)..LocalDate(2025, 8, 16),
)

fun main(): Unit = runBlocking {
    Logger.log("Running…")
    searchForAvailableRooms()
    Logger.log("Finished script.")
    httpClient.close()
}

private suspend fun searchForAvailableRooms() {
    val availabilities = getKaerlingerHausAvailabilities()
        ?.filterForPreferredDates()
        ?.filterForAtLeastTwoBedsPerCategory()
        .log("Available rooms after filtering")

    if (availabilities.isNullOrEmpty()) return

    val formattedAvailabilities = availabilities.joinToString("\n", transform = Availability::format)

    notify(" **Verfügbare Zimmer im Kärlingerhaus gefunden!**\n\n$formattedAvailabilities")
}

private fun List<Availability>.filterForAtLeastTwoBedsPerCategory(): List<Availability>? = this.filter { availability ->
    availability.availableDoubleRoomBeds >= 2 ||
            availability.availableMattressRoomBeds >= 2 ||
            availability.availableDormBeds >= 2
}

private fun List<Availability>?.filterForPreferredDates(): List<Availability>? = this?.filter { availability ->
    preferredDates.any { dateRange ->
        availability.date in dateRange
    }
}

private fun List<Availability>?.log(message: String): List<Availability>? {
    Logger.log("$message: $this")
    return this
}

private fun LocalDate.format(): String = "$dayOfMonth.$monthNumber.$year"
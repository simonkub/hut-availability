import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

object Logger {
    private val logFile: Path = "logs/availability_log.txt".toPath()

    fun log(message: String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        val message = "[$now] $message\n"

        FileSystem.Companion.SYSTEM.appendingSink(logFile).buffer().use {
            it.writeUtf8(message)
        }

        println(message)
    }
}
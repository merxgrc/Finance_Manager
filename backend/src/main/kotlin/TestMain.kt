import java.sql.DriverManager.println
import kotlin.text.toIntOrNull

fun main() {
    val testString = "123"
    println(testString.toIntOrNull())  // ✅ Should print: 123
}


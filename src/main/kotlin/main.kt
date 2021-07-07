import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.concurrent.fixedRateTimer

private val _clock = MutableStateFlow(0)
val clock = _clock.asStateFlow()

fun main(args: Array<String>) {
    showMenu()
}

fun showMenu() {
    println(".".repeat(30))
    println("Inter '1' For Set Clock Length")
    println("Inter '2' For Choose Machine Code File")

    val menu = readLine()

    if (menu.isNullOrEmpty() || !menu.all { c -> c == '1' || c == '2' }) {
        println("******* Please Inter Valid Menu Number *******")
        showMenu()
    } else {
        when (menu.toInt()) {
            1 -> {
                showClockLengthMenu()
            }
            2 -> {
                showFilePicker()
            }
        }
    }
}

fun showFilePicker() {

}

fun showClockLengthMenu() {
    println("*".repeat(30))
    println("please inter clock length in microseconds between 1~1000")

    val clockLength = readLine()

    println("settings is saved")

    showMenu()
}

fun startClock(){
    fixedRateTimer(startAt = Calendar.getInstance().time, period = 1){
        ++_clock.value
    }
}



import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import stages.StageDecode
import stages.StageFetch
import utils.convertBytesToUInt
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@Inject
lateinit var stageFetch: StageFetch

@Inject
lateinit var stageDecode: StageDecode

fun main(args: Array<String>) {
    //showMenu()

    val x = BitSet(32)
    x.set(0, 8, true)
    val y = x.toByteArray()

    println(convertBytesToUInt(y?.toList()!!))
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

fun startClock() {
    fixedRateTimer(startAt = Calendar.getInstance().time, period = 1) {
        stageFetch.fetchFromInstructionMemory()
        stageDecode.decodeInstruction()
    }
}



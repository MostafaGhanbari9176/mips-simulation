import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.ALUOperator
import pipline_registers.EXMEMRegister
import pipline_registers.IDEXRegister
import pipline_registers.IFIDRegister
import pipline_registers.MEMWBRegister
import stages.*
import utils.colored
import java.util.*
import kotlin.concurrent.fixedRateTimer

var timer: Timer? = null
private var stageFetch: StageFetch? = null
private var stageDecode: StageDecode? = null
private var stageExecute: StageExecute? = null
private var stageMemory: StageMemory? = null
private var stageWriteBack: StageWriteBack? = null
var programIsEnd = false
private val clock = MutableStateFlow(0)
private var numberOfActivatedStages = 0

private var aluOperator: ALUOperator = ALUOperator.Add

fun main(args: Array<String>) {
    colored {
        showMenu()
    }
}

private fun showMenu() {
    println(".".repeat(30))
    println("Please Choose One")
    println("1- Set Clock Length(not implemented!)")
    println("2- Run A Simple Program Base On Section One(Table One) Of Final Project")

    val input = readLine()

    if (validateSelectedMenu(input, 1..2, ::showMenu)) {
        when (input!!.toInt()) {
            1 -> {
                showClockLengthMenu()
            }
            2 -> {
                showTableOneInstructionsMenu()
            }
        }
    }
}

private fun showTableOneInstructionsMenu() {
    println(".".repeat(30))
    println("Please Choose One")
    println("1- Add")
    println("2- Sub")
    println("3- OR")
    println("4- And")
    println("5- SLT")
    println("6- Main Menu")

    val input = readLine()

    if (validateSelectedMenu(input, 1..6, ::showTableOneInstructionsMenu)) {
        aluOperator = when (input!!.toInt()) {
            1 -> ALUOperator.Add
            2 -> ALUOperator.Sub
            3 -> ALUOperator.OR
            4 -> ALUOperator.And
            5 -> ALUOperator.SLT
            else -> {
                showMenu(); ALUOperator.SLT
            }
        }

        readOperands()

    }
}

fun readOperands() {
    println(".".repeat(30))
    println("Please Inter Operands And separate with 'o' Like 5o7")
    println("Or Inter exit")

    var input = readLine()

    if (input == "exit") {
        showMenu()
        return
    }

    val separatorIndex: Int = input?.indexOf('o', ignoreCase = true) ?: -1
    if (separatorIndex != -1) {
        input = input!!.replace("o", "", ignoreCase = true)
        if (isDigitOnly(input)) {
            val data1 = (input as java.lang.String).substring(0, separatorIndex).toInt()
            val data2 = input.substring(separatorIndex).toInt()

            startTestALUProgram(data1, data2)

            return
        }
    }

    println("Input Is Not Valid")
    readOperands()

}

fun startTestALUProgram(data1: Int, data2: Int) {
    instantiateStages()

    stageMemory?.loadDataMemory(data1, data2)
    stageFetch?.loadALUInstructions(aluOperator)
    startClock()
    activateRegisters()
    activateStages()
}

fun activateStages() {
    CoroutineScope(IO).launch {
        stageFetch = StageFetch().apply {
            ++numberOfActivatedStages
            activate(clock as StateFlow<Int>) {
                programIsEnd()
                timer?.cancel()
            }
        }
    }
    CoroutineScope(IO).launch {
        ++numberOfActivatedStages
        stageDecode = StageDecode().apply {
            activate(clock as StateFlow<Int>)
        }
    }
    CoroutineScope(IO).launch {
        ++numberOfActivatedStages
        stageExecute = StageExecute().apply {
            activate(clock as StateFlow<Int>)
        }
    }
    CoroutineScope(IO).launch {
        ++numberOfActivatedStages
        stageMemory = StageMemory().apply {
            activate(clock as StateFlow<Int>)
        }
    }
}

private fun activateRegisters() {
    IFIDRegister().activateRegister(clock as StateFlow<Int>)
    IDEXRegister().activateRegister(clock as StateFlow<Int>)
    EXMEMRegister().activateRegister(clock as StateFlow<Int>)
    MEMWBRegister().activateRegister(clock as StateFlow<Int>)
}

fun instantiateStages() {
    stageFetch = StageFetch()
    stageDecode = StageDecode()
    stageExecute = StageExecute()
    stageMemory = StageMemory()
    stageWriteBack = StageWriteBack()
}

private fun startClock() {
    timer = fixedRateTimer(startAt = Calendar.getInstance().time, period = 1000) {
        if (numberOfActivatedStages == 4)
            ++clock.value
    }
}

private fun showFilePicker() {

}

private fun showClockLengthMenu() {
    println("*".repeat(30))
    println("please inter clock length in microseconds between 1~1000")

    val clockLength = readLine()

    println("settings is saved")

    showMenu()
}

private fun programIsEnd() {
    val aluResult = stageMemory?.readDataMEM(2)
    println("=".repeat(35))
    println("Program Is End")
    println("ALU Result From MEM[2] : $aluResult")
    // showMenu()
}

private fun validateSelectedMenu(input: String?, validRange: IntRange, menu: () -> Unit): Boolean {
    val valid = !input.isNullOrEmpty() && input.all { c ->
        c.isDigit() && validRange.contains(c.toString().toInt())
    }

    if (!valid) {
        println(".".repeat(30))
        println("******* Please Inter Valid Menu Number *******")
        menu()
    }

    return valid
}

private fun isDigitOnly(input: String): Boolean {
    return input.all { c -> c.isDigit() }
}



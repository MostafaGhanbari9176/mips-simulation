import model.ALUOperator
import stages.*
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@Inject
lateinit var stageFetch: StageFetch

@Inject
lateinit var stageDecode: StageDecode

@Inject
lateinit var stageExecute: StageExecute

@Inject
lateinit var stageMemory: StageMemory

@Inject
lateinit var stageWriteBack: StageWriteBack

private var aluOperator: ALUOperator = ALUOperator.Add

fun main(args: Array<String>) {
    showMenu()
}

private fun showMenu() {
    println(".".repeat(30))
    println("Please Choose One")
    println("1- Set Clock Length")
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

    if (validateSelectedMenu(input, 1..5, ::showTableOneInstructionsMenu)) {
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

    if(input == "exit"){
        showMenu()
        return
    }

    val separatorIndex: Int = input?.indexOf('o', ignoreCase = true) ?: -1
    if (separatorIndex != -1) {
        input = input!!.replace("o", "", ignoreCase = true)
        if (isDigitOnly(input)) {
            val data1 = input.substring(0, separatorIndex).toInt()
            val data2 = input.substring(separatorIndex - 1).toInt()

            loadDataMemory(data1,data2)
            loadALUInstructionMemory()

            return
        }
    }

    println("Input Is Not Valid")
    readOperands()

}

fun loadDataMemory(vararg datas:Int) {
    stageMemory.loadDataMemory(datas)
}

fun loadALUInstructionMemory() {
    stageFetch.loadALUInstructions(aluOperator)
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

private fun startClock() {
    fixedRateTimer(startAt = Calendar.getInstance().time, period = 1) {
        stageFetch.fetchFromInstructionMemory()
        stageDecode.decodeInstruction()
        stageExecute.executeInstruction()
        stageMemory.applyMemWork()
        //stageWriteBack
    }
}

private fun validateSelectedMenu(input: String?, validRange: IntRange, menu: () -> Unit): Boolean {
    val valid = !input.isNullOrEmpty() && input.all { c ->
        c.isDigit() && validRange.contains(c.toInt())
    }

    if (!valid) {
        println("******* Please Inter Valid Menu Number *******")
        menu()
    }

    return valid
}

private fun isDigitOnly(input: String): Boolean {
    return input.all { c -> c.isDigit() }
}



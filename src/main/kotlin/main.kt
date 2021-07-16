
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
private val clock = MutableStateFlow(0)

val stageFetch = StageFetch()
val stageDecode = StageDecode()
val stageExecute = StageExecute()
val stageMemory = StageMemory()
val stageWriteBack = StageWriteBack()

val if_id = IFIDRegister()
val id_ex = IDEXRegister()
val ex_mem = EXMEMRegister()
val mem_wb = MEMWBRegister()

private var aluOperator: ALUOperator = ALUOperator.Add
var beq: Boolean = false

private var numbersCount = 0

fun main(args: Array<String>) {
    colored {
        showMenu()
    }
}

private fun showMenu() {
    println(".".repeat(30))
    println("Please Choose One")
    println("0- Find Big and Small Number")
    println("1- Set Clock Length(not implemented!)")
    println("2- Run A Simple Program For Testing ALU Operators")
    println("3- Run A Simple Program For Testing Jump (True Result => 1030)")
    println("4- Run A Simple Program For Testing BEQ (Taken => 200, NotTaken => 300)")
    println("5- Run A Simple Program For Testing BNE (Taken => 200, NotTaken => 300)")
    val input = readLine()

    if (validateSelectedMenu(input, 0..5, ::showMenu)) {
        when (input!!.toInt()) {
            0 -> getNumbers()
            1 -> {
                showClockLengthMenu()
            }
            2 -> {
                showTableOneInstructionsMenu()
            }
            3 -> startJumpTest()
            4 -> {
                beq = true
                readOperands(::startBranchTest)
            }
            5 -> {
                beq = false
                readOperands(::startBranchTest)
            }
        }
    }
}

fun getNumbers() {
    println(".".repeat(30))
    println("Please Inter Numbers And Separate With ';' Like 1;2;20")
    println("Or Inter exit")
    val input = readLine()

    if (input == "exit") {
        showMenu()
        return
    }

    if (input.isNullOrEmpty() || !isDigitOnly(input, ';')) {
        getNumbers()
        return
    }

    val numbers = input.split(';').map { i -> i.toInt() }.toList()
    startFinder(numbers)
}

private fun showTableOneInstructionsMenu() {
    println(".".repeat(30))
    println("Please Choose One")
    println("1- Add")
    println("2- Sub")
    println("3- OR")
    println("4- And")
    println("5- SLT")
    println("6- AddI")
    println("7- SltI")
    println("8- AndI")
    println("9- OrI")
    println("10- Main Menu")

    val input = readLine()

    if (validateSelectedMenu(input, 1..10, ::showTableOneInstructionsMenu)) {
        aluOperator = when (input!!.toInt()) {
            1 -> ALUOperator.Add
            2 -> ALUOperator.Sub
            3 -> ALUOperator.OR
            4 -> ALUOperator.And
            5 -> ALUOperator.SLT
            6 -> ALUOperator.AddI
            7 -> ALUOperator.SltI
            8 -> ALUOperator.AndI
            9 -> ALUOperator.OrI
            else -> ALUOperator.None
        }
        if (aluOperator == ALUOperator.None)
            showMenu()
        else if (input.toInt() > 5)
            readITypeOperand()
        else
            readOperands(::startTestALUProgram)
    }
}

private fun readITypeOperand() {
    println(".".repeat(30))
    println("Please Inter One Operand (Immediate Value => 10)")
    println("Or Inter exit")

    val input = readLine()

    if (input == "exit") {
        showMenu()
        return
    }

    if (input.isNullOrEmpty() || !isDigitOnly(input))
        readITypeOperand()
    else
        startTestALUProgram(input.toInt(), input.toInt())
}

private fun readOperands(targetFunction:(d1:Int, d2:Int) -> Unit) {
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

            targetFunction(data1, data2)

            return
        }
    }

    println("Input Is Not Valid")
    readOperands(targetFunction)

}

private fun startTestALUProgram(data1: Int, data2: Int) {
    stageMemory.loadDataMemory(data1, data2)
    stageFetch.loadALUInstructions(aluOperator)
    turnON()
}

private fun startJumpTest(){
    stageFetch.loadJumpTestInstruction()
    turnON()
}

private fun startBranchTest(data1: Int, data2: Int){
    stageMemory.loadDataMemory(data1, data2)
    stageFetch.loadBranchTestInstruction(beq)
    turnON()
}

private fun startFinder(data:List<Int>){
    numbersCount = data.size
    stageMemory.loadDataMemory(listOf<Int>(data.size).plus(data))
    stageFetch.loadFinderProgram()
    turnON()
}

private fun turnON(){
    stageFetch.activatePC(clock as StateFlow<Int>)
    activateRegisters()
    startClock()
}

private fun activateRegisters() {
    //if_id.activateRegister(clock as StateFlow<Int>)
    id_ex.activateRegister(clock as StateFlow<Int>)
    ex_mem.activateRegister(clock as StateFlow<Int>) {
        programIsEnd()
        timer?.cancel()
    }
    mem_wb.activateRegister(clock as StateFlow<Int>)
}

private fun startClock() {
    timer = fixedRateTimer(startAt = Calendar.getInstance().time, period = 200) {
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
    val aluResult = stageMemory.readDataMEM(2)
    colored {
        println("=--=".repeat(15).yellow.bold.reverse)
        println(" Program Is End ".yellow.bold.reverse)
        println(" ALU Result From MEM[2] : $aluResult ".yellow.bold.reverse)
        println(" Result From MEM[${numbersCount + 2}] : ${stageMemory.readDataMEM(numbersCount + 2)} ".yellow.bold.reverse)
        println("=--=".repeat(15).yellow.bold.reverse)
    }
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

private fun isDigitOnly(input: String, additionChar:Char): Boolean {
    return input.all { c -> c.isDigit() || c == additionChar }
}



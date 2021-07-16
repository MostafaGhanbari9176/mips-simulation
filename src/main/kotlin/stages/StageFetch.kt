package stages

import ex_mem
import if_id
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.ALUOperator
import model.InstructionModel
import model.PCSource
import utils.*

class StageFetch {

    companion object {
        private var PC: Int = 0
        private val instructionMemory = mutableListOf<InstructionModel>()
        private var disablePC = false
        private var programIsEnd = false
    }

    fun activatePC(clock: StateFlow<Int>) {
        CoroutineScope(IO).launch {
            clock.collect { i ->
                fetchFromInstructionMemory(i)
            }
        }
    }

    private fun fetchFromInstructionMemory(clock: Int) {
        if (programIsEnd)
            return

        if_id.activateRegister(clock)

        checkForBranch()

        val instruction = instructionMemory[PC]
        colored {
            println("fetch instruction:${instruction.id} on clock:$clock".red.bold)
        }

        if (!disablePC) {
            ++PC
            //fill IF/ID register
            if_id.apply {
                storeNextPC(PC)
                storeInstruction(instruction)
            }
        }
    }

    fun disablePC(dis: Boolean, instID: Int) {
        colored {
            println("DisablePC:$dis inst:$instID".bold.reverse)
        }
        disablePC = dis
    }

    fun programIsEnd() {
        programIsEnd = true
    }

    private fun checkForBranch() {
        val pcSource = ex_mem.getPCSource()
        if (pcSource == PCSource.Branch) {
            val targetAddress = ex_mem.getBranchTarget()
            colored {
                println("(B)Change PC From:$PC To:$targetAddress".bold.red.reverse)
            }
            PC = targetAddress
        }

        if(ex_mem.getIsBranchFlag())
            disablePC(false, -2)
    }

    fun loadALUInstructions(aluOperator: ALUOperator) {
        instructionMemory.clear()

        val instructions = listOf<String>(
            "10001100000010000000000000000000",
            "10001100000010010000000000000001",
            when (aluOperator) {
                ALUOperator.Add -> "00000001001010000101000000100000"
                ALUOperator.Sub -> "00000001001010000101000000100010"
                ALUOperator.OR -> "00000001001010000101000000100101"
                ALUOperator.And -> "00000001001010000101000000100100"
                ALUOperator.SLT -> "00000001001010000101000000101010"
                ALUOperator.AddI -> "00100001000010100000000000001010"
                ALUOperator.SltI -> "00101001000010100000000000001010"
                ALUOperator.AndI -> "00110001000010100000000000001010"
                ALUOperator.OrI -> "00110101000010100000000000001010"
                ALUOperator.None -> TODO()
            },
            "10101100000010100000000000000010",
            "11111111111111111111111111111111",
            "11111111111111111111111111111111"
        )

        instructionMemory.addAll(
            instructions.mapIndexed { index, inst ->
                InstructionModel(inst, index)
            }
        )

    }

    fun loadJumpTestInstruction() {
        instructionMemory.clear()

        val instructions = listOf<String>(
            "00100000000010000000000000001010",
            "00100000000010000000000000010100",
            "00100000000010000000000000011110",
            "00001000000000000000000000001010",
            "00100000000010000000000000110010",
            "00100000000010000000000000111100",
            "00100000000010000000000001000110",
            "00100000000010000000000001010000",
            "00100000000010000000000001011010",
            "00100000000010000000000001100100",
            "00100001000010000000001111101000",
            "10101100000010000000000000000010",
            "11111111111111111111111111111111",
            "11111111111111111111111111111111"
        )

        instructionMemory.addAll(
            instructions.mapIndexed { index, inst ->
                InstructionModel(inst, index)
            }
        )
    }

    fun loadBranchTestInstruction(beq: Boolean) {
        instructionMemory.clear()

        val instructions = listOf<String>(
            "10001100000010000000000000000000",
            "10001100000010010000000000000001",
            "${if (beq) "000100" else "000101"}01000010010000000000000001",
            "00100000000010100000000001100100",
            "00100001010010100000000011001000",
            "10101100000010100000000000000010",
            "11111111111111111111111111111111",
            "11111111111111111111111111111111"
        )

        instructionMemory.addAll(
            instructions.mapIndexed { index, inst ->
                InstructionModel(inst, index)
            }
        )
    }

    fun changePC(targetAddress: Int) {
        colored {
            println("(J)Change PC From:$PC To:$targetAddress".bold.red.reverse)
        }
        PC = targetAddress
    }

}


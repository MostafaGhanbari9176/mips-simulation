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
        if(programIsEnd)
            return

        if_id.activateRegister(clock)

        val instruction = instructionMemory[PC]

        if (!disablePC) {
            colored {
                println("fetch instruction:${instruction.id} on clock:$clock".red.bold)
            }
            PC = when (getPCSource()) {
                PCSource.NextPC -> ++PC
                PCSource.Branch -> ex_mem.getBranchAddress()
            }
        }

        //fill IF/ID register
        if_id.apply {
            storeNextPC(PC)
            storeInstruction(instruction)
        }
    }

    fun disablePC(dis: Boolean) {
        disablePC = dis
    }

    fun programIsEnd(end:Boolean){
        programIsEnd = true
    }

    private fun getPCSource(): PCSource {
        val isBranch = ex_mem.getIsBranchFlag()
        val zeroFlag = ex_mem.getZeroFlag()

        return if (isBranch && zeroFlag)
            PCSource.Branch
        else
            PCSource.NextPC
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
                        ALUOperator.OrI  -> "00110101000010100000000000001010"
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

}


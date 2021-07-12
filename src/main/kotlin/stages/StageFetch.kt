package stages

import ex_mem
import if_id
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.ALUOperator
import model.InstructionModel
import model.PCSource
import programIsEnd
import utils.colored
import utils.stallInstruction

class StageFetch {

    companion object {
        private var PC: Int = 0
        private val instructionMemory = mutableListOf<InstructionModel>()
        private var stall = false
    }

    fun activatePC(clock:StateFlow<Int>){
        CoroutineScope(IO).launch {
            clock.collect { i ->
                fetchFromInstructionMemory(i)
            }
        }
    }

    private fun fetchFromInstructionMemory(clock:Int) {
        if_id.activateRegister(clock)

        var instruction = instructionMemory[PC]
        if (checkForLastInst(instruction.inst))
            return

        if (!stall) {
            colored {
                println("fetch instruction:${instruction.id} on clock:$clock".red.bold)
            }
            PC = when (getPCSource()) {
                PCSource.NextPC -> ++PC
                PCSource.Branch -> ex_mem.getBranchAddress()
            }
        } else{
            stall = false
            instruction = stallInstruction
        }

        //fill IF/ID register
        if_id.apply {
            storeNextPC(PC)
            storeInstruction(instruction)
        }
    }

    fun injectStall() {
        stall = true
    }

    private fun checkForLastInst(instruction: String): Boolean {
        val isLastIns =  instruction.all { c -> c == '1' }

        programIsEnd = true

        return isLastIns
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
            "00000001001010000101000000" +
                    when (aluOperator) {
                        ALUOperator.Add -> "100000"
                        ALUOperator.Sub -> "100010"
                        ALUOperator.OR -> "100101"
                        ALUOperator.And -> "100100"
                        ALUOperator.SLT -> "101010"
                    },
            "10101100000010100000000000000010",
            "11111111111111111111111111111111"
        )

        instructionMemory.addAll(
            instructions.mapIndexed { index, inst ->
                InstructionModel(inst, index)
            }
        )

    }

}


package stages

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import model.ALUOperator
import model.InstructionModel
import model.PCSource
import pipline_registers.EXMEMRegister
import pipline_registers.IFIDRegister
import utils.colored
import utils.stallInstruction

class StageFetch {

    private val exMemRegister = EXMEMRegister()
    private val ifIDRegister = IFIDRegister()

    companion object {
        private var PC: Int = 0
        private val instructionMemory = mutableListOf<InstructionModel>()
        private var stall = false
    }

    suspend fun activate(clock:StateFlow<Int>, programIsEnd: () -> Unit){
        clock.collect { i ->
           fetchFromInstructionMemory(i, programIsEnd)
        }
    }

    private fun fetchFromInstructionMemory(clock:Int, programIsEnd: () -> Unit) {
        var instruction = instructionMemory[PC]
        if (programIsEnd(instruction.inst))
            programIsEnd()

        if (!stall) {
            colored {
                println("fetch instruction:${instruction.id} on clock:$clock".red.bold)
            }
            PC = when (getPCSource()) {
                PCSource.NextPC -> ++PC
                PCSource.Branch -> exMemRegister.getBranchAddress()
            }
        } else{
            stall = false
            instruction = stallInstruction
        }

        //fill IF/ID register
        ifIDRegister.apply {
            storeNextPC(PC)
            storeInstruction(instruction)
        }
    }

    fun injectStall() {
        stall = true
    }

    private fun programIsEnd(instruction: String): Boolean {
        return instruction.all { c -> c == '1' }
    }

    private fun getPCSource(): PCSource {
        val isBranch = exMemRegister.getIsBranchFlag()
        val zeroFlag = exMemRegister.getZeroFlag()

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


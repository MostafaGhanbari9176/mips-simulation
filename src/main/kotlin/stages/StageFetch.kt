package stages

import model.ALUOperator
import model.PCSource
import pipline_registers.EXMEMRegister
import pipline_registers.IFIDRegister

class StageFetch {

    private val exMemRegister = EXMEMRegister()
    private val ifIDRegister = IFIDRegister()

    companion object {
        private var PC: Int = 0
        private val instructionMemory = mutableListOf<String>()
    }

    fun fetchFromInstructionMemory(programIsEnd: () -> Unit) {
        val instruction = instructionMemory[PC]
        if (programIsEnd(instruction))
            programIsEnd()

        PC = when (getPCSource()) {
            PCSource.NextPC -> ++PC
            PCSource.Branch -> exMemRegister.getBranchAddress()
        }
        //fill IF/ID register
        ifIDRegister.apply {
            storeNextPC(PC)
            storeInstruction(instruction)
        }
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
            "00000000000000000000000000000000",
            "00000001001010000101000000" +
                    when (aluOperator) {
                        ALUOperator.Add -> "100000"
                        ALUOperator.Sub -> "100010"
                        ALUOperator.OR -> "100101"
                        ALUOperator.And -> "100100"
                        ALUOperator.SLT -> "101010"
                    },
            "00000000000000000000000000000000",
            "10101100000010100000000000000010",
            "11111111111111111111111111111111"
        )

        instructionMemory.addAll(instructions)

    }

}


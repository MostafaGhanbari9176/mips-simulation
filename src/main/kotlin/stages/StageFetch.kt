package stages

import model.ALUOperator
import model.PCSource
import pipline_registers.EXMEMRegister
import pipline_registers.IFIDRegister
import java.util.*
import javax.inject.Inject

class StageFetch @Inject constructor() {

    @Inject
    lateinit var stageMemory: StageMemory

    @Inject
    lateinit var exMemRegister: EXMEMRegister

    @Inject
    lateinit var ifIDRegister: IFIDRegister

    companion object {
        private var PC: Int = 0
        private val instructionMemory = mutableListOf<BitSet>()
    }

    fun fetchFromInstructionMemory() {
        val instruction = instructionMemory[PC]

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

        instructions.forEach { inst ->
            instructionMemory.add(stringToBitSet(inst))
        }
    }

    fun stringToBitSet(data: String): BitSet {
        val result = BitSet(32)
        for (i in data.indices) {
            result.set(i, data[i] == '1')
        }
        return result
    }

}


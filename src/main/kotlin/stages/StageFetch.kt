package stages

import model.PCSource
import pipline_registers.EXMEMRegister
import pipline_registers.IFIDRegister
import java.util.*
import javax.inject.Inject

class StageFetch() {

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

}
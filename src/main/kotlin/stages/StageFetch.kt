package stages

import clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
    lateinit var ifIDRegister:IFIDRegister

    companion object {
        private var PC: Int = 0
        private val instructionMemory = mutableListOf<BitSet>()
    }

    init {
        CoroutineScope(IO).launch {
            clock.collect { _ ->
                fetchFromInstructionMemory()
            }
        }
    }

    private fun fetchFromInstructionMemory() {
        val instruction = instructionMemory[PC]

        PC = when (stageMemory.pcSource()) {
            PCSource.NextPC -> ++PC
            PCSource.Branch -> exMemRegister.getBranchAddress()
        }

        ifIDRegister.apply {
            storeNextPC(PC)
            storeInstruction(instruction)
        }
    }

}
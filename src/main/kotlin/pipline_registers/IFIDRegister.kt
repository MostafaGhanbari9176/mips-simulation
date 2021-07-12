package pipline_registers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.InstructionModel
import utils.colored
import utils.stallInstruction
import java.util.*

class IFIDRegister {

    companion object {
        private var nextPC_IN: Int = 0
        private var instruction_IN = stallInstruction

        private var nextPC_OUT: Int = 0
        private var instruction_OUT = stallInstruction
    }

    fun activateRegister(clock: StateFlow<Int>) {
        CoroutineScope(IO).launch {
            clock.collect {i->
                copyInputToOutPut(i)
            }
        }
    }

    private fun copyInputToOutPut(clock:Int) {
        nextPC_OUT = nextPC_IN
        instruction_OUT = instruction_IN

        colored {
            println("IF/ID on clock $clock ; instIN:${instruction_IN.id}".bold)
        }
    }

    fun storeNextPC(nextPC: Int) {
        IFIDRegister.nextPC_IN = nextPC
    }

    fun storeInstruction(inst: InstructionModel) {
        instruction_IN = inst
    }

    fun getInstruction() = instruction_OUT

    fun getNextPC() = nextPC_OUT

}
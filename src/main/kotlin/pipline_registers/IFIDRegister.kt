package pipline_registers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.InstructionModel
import stageDecode
import utils.colored
import utils.stallInstruction
import java.util.*

class IFIDRegister {

    companion object {
        private var nextPC_IN: Int = 0
        private var instruction_IN = stallInstruction
        private var endSignal_IN = false

        private var nextPC_OUT: Int = 0
        private var instruction_OUT = stallInstruction
        private var endSignal_OUT = false

        private var disable = false
    }

    fun activateRegister(i: Int) {
        //CoroutineScope(IO).launch {
        //   clock.collect {i->
        if (!disable)
            copyInputToOutPut(i)
        stageDecode.decodeInstruction(i)
        //     }
        // }
    }

    private fun copyInputToOutPut(clock: Int) {
        nextPC_OUT = nextPC_IN
        instruction_OUT = instruction_IN
        endSignal_OUT = endSignal_IN

        colored {
            println("IF/ID on clock $clock ; instIN:${instruction_IN.id}".bold)
        }
    }

    fun storeNextPC(nextPC: Int) {
        nextPC_IN = nextPC
    }

    fun storeInstruction(inst: InstructionModel) {
        instruction_IN = inst
    }

    fun getInstruction() = instruction_OUT

    fun getNextPC() = nextPC_OUT

    fun disable(dis: Boolean) {
        disable = dis
    }

    fun resetIFID() {
        nextPC_IN = 0
        instruction_IN = stallInstruction
        endSignal_IN = false
    }

}



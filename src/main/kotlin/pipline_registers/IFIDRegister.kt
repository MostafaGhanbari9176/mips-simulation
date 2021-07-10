package pipline_registers

import model.InstructionModel
import utils.stallInstruction
import java.util.*

class IFIDRegister {

    companion object{
        private var nextPC:Int = 0
        private var instruction = stallInstruction
    }

    fun storeNextPC(nextPC:Int){
        IFIDRegister.nextPC = nextPC
    }

    fun storeInstruction(inst: InstructionModel){
        instruction = inst
    }

    fun getInstruction() = instruction

    fun getNextPC() = nextPC

}
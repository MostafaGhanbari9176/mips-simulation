package pipline_registers

import java.util.*
import javax.inject.Inject

class IFIDRegister @Inject constructor() {

    companion object{
        private var nextPC:Int = 0
        private var instruction = BitSet(32)
    }

    fun storeNextPC(nextPC:Int){
        IFIDRegister.nextPC = nextPC
    }

    fun storeInstruction(inst:BitSet){
        IFIDRegister.instruction = inst
    }

    fun getInstruction() = instruction

    fun getNextPC() = nextPC

}
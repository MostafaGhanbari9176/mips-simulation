package pipline_registers

import java.util.*

class IFIDRegister {

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
package pipline_registers

import java.util.*

class IFIDRegister {

    companion object{
        private var nextPC:Int = 0
        private var instruction:String = "00000000000000000000000000000000"
    }

    fun storeNextPC(nextPC:Int){
        IFIDRegister.nextPC = nextPC
    }

    fun storeInstruction(inst:String){
        instruction = inst
    }

    fun getInstruction() = instruction

    fun getNextPC() = nextPC

}
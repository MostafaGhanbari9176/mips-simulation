package pipline_registers

class IFIDRegister {

    companion object{
        private var nextPC:Int = 0
        private val instruction = mutableListOf<Byte>()
    }

    fun setNextPC(nextPC:Int){
        IFIDRegister.nextPC = nextPC
    }

}
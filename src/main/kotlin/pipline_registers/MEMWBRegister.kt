package pipline_registers

import model.RFWritePortSource

class MEMWBRegister {

    companion object{
        private var registerWrite = false
        private var dataMemoryOutPut:Int = 0
        private var aluResult:Int = 0
        private var registerDestination = 0
        private var rfWritePortSource = RFWritePortSource.AluResult
    }

}
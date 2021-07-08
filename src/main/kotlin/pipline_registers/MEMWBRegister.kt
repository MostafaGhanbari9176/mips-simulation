package pipline_registers

import model.RFWritePortSource

class MEMWBRegister {

    companion object {
        private var registerWrite = false
        private var dataMemoryOutPut: Int = 0
        private var aluResult: Int = 0
        private var registerDestination = 0
        private var rfWritePortSource = RFWritePortSource.AluResult
    }

    fun getWritingOnRegisterFlag(): Boolean = registerWrite

    fun getRegisterDestination() = registerDestination

    fun storeDataMemOutPut(data:Int){
        dataMemoryOutPut = data
    }

    fun storeRegisterDestination(destination: Int) {
        registerDestination = destination
    }

    fun storeALUResult(result: Int) {
        aluResult = result
    }

    fun storeRegisterWriteFlag(store: Boolean) {
        registerWrite = store
    }

    fun storeRFStorePortSource(source:RFWritePortSource){
        rfWritePortSource = source
    }

    fun getRFStorePortSource() = rfWritePortSource

    fun getALUResult() = aluResult

    fun getDataMemOutPut() = dataMemoryOutPut

}


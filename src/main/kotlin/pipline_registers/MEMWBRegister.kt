package pipline_registers

import model.InstructionModel
import model.RFWritePortSource
import utils.stallInstruction

class MEMWBRegister {

    companion object {
        private var registerWrite = false
        private var dataMemoryOutPut: Int = 0
        private var aluResult: Int = 0
        private var rfWriteAddress = 0
        private var rfWritePortSource = RFWritePortSource.AluResult
        private var instruction = stallInstruction
    }

    fun getWritingOnRegisterFlag(): Boolean = registerWrite

    fun getRFWriteAddress() = rfWriteAddress

    fun storeDataMemOutPut(data:Int){
        dataMemoryOutPut = data
    }

    fun storeRFWriteAddress(address: Int) {
        rfWriteAddress = address
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

    fun storeInstruction(inst: InstructionModel) {
        instruction = inst
    }

    fun getInstruction() = instruction

}


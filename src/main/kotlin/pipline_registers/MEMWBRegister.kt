package pipline_registers

import model.RFWritePortSource
import javax.inject.Inject

class MEMWBRegister @Inject constructor() {

    companion object {
        private var registerWrite = false
        private var dataMemoryOutPut: Int = 0
        private var aluResult: Int = 0
        private var registerDestination = 0
        private var rfWritePortSource = RFWritePortSource.AluResult
    }

    fun getWritingOnRegisterFlag(): Boolean = registerWrite

    fun getRegisterDestination() = registerDestination

}
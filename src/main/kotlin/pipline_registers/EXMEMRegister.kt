package pipline_registers

import model.RFWritePortSource

class EXMEMRegister {

    companion object {
        private var registerWrite = false
        private var memoryWrite = false
        private var memoryRead = false
        private var isBranch = false
        private var branchAddress = 0
        private var aluZeroFlag = false
        private var aluResult = 0
        private var readPortTwoOfRF = 0
        private var rfWriteAddress = 0
        private var rfWritePortSource = RFWritePortSource.AluResult
    }

    fun getBranchAddress() = branchAddress

    fun storeALUResult(data: Int) {
        aluResult = data
    }

    fun storeZeroFlag(zeroFlag: Boolean) {
        aluZeroFlag = zeroFlag
    }

    fun storeReadPortTwoData(data:Int){
        readPortTwoOfRF = data
    }

    fun storeRFWriteAddress(data: Int) {
        rfWriteAddress = data
    }

    fun storeIsBranchFlag(itIs: Boolean) {
        isBranch = itIs
    }

    fun storeMemWriteFlag(write: Boolean) {
        memoryWrite = write
    }

    fun storeMemReadFlag(read: Boolean) {
        memoryRead = read
    }

    fun storeWritingOnRegisterFlag(write: Boolean) {
        registerWrite = write
    }

    fun storeBranchAddress(address: Int) {
        branchAddress = address
    }

    fun getMemWriteFlag() = memoryWrite

    fun getALUResult() = aluResult

    fun getMemWriteData() = readPortTwoOfRF

    fun getMemReadFlag() = memoryRead

    fun getIsBranchFlag() = isBranch

    fun getZeroFlag() = aluZeroFlag

    fun getRFWriteAddress() = rfWriteAddress

    fun getRegisterWriteFalg() = registerWrite

    fun storeRegisterWritePortSource(source: RFWritePortSource) {
        rfWritePortSource = source
    }

    fun getWritePortSource() = rfWritePortSource

}






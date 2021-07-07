package pipline_registers

import javax.inject.Inject

class EXMEMRegister @Inject constructor() {

    companion object {
        private var registerWrite = false
        private var memoryWrite = false
        private var memoryRead = false
        private var isBranch = false
        private var branchAddress = 0
        private var aluZeroFlag = false
        private var aluResult = 0
        private var readPortTwoOfRF = 0
        private var registerDestination = 0
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

    fun storeRegisterDestination(data: Int) {
        registerDestination = data
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

}






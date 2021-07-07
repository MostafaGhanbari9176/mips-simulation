package pipline_registers

import javax.inject.Inject

class EXMEMRegister @Inject constructor() {

    companion object{
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

}
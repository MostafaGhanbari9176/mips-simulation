package pipline_registers

class EXMEMRegister {

    companion object{
        private var registerWrite = false
        private var memoryWrite = false
        private var memoryRead = false
        private var isBranch = false
        private var aluZeroFlag = false
        private var aluResult = 0
        private var readPortTwoOfRF = 0
        private var registerDestination = 0
    }

}
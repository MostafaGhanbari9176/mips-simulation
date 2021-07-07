package pipline_registers

import model.ALUOperand
import model.ALUSource
import model.WriteBackDestination

class IDEXRegister {

    companion object {
        private var registerWrite = false
        private var memoryWrite = false
        private var memoryRead = false
        private var isBranch = false
        private var writeBackDestination = WriteBackDestination.RTypeDestination
        private var aluOperand = ALUOperand.Add
        private var aluSource = ALUSource.ReadPortTwoOFRF
        private var nextPC = 0
        private var readPortOneOfRF:Int = 0
        private var readPortTwoOfRF:Int = 0
        private var instructionImmediateSection:Int = 0
        private var ITypeDestination = 0
        private var RTypeDestination = 0
    }

}
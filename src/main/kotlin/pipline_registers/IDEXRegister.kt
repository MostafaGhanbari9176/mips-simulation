package pipline_registers

import model.ALUOperator
import model.ALUSource
import model.WriteBackDestination
import javax.inject.Inject

class IDEXRegister @Inject constructor() {

    companion object {
        private var registerWrite = false
        private var memoryWrite = false
        private var memoryRead = false
        private var isBranch = false
        private var writeBackDestination = WriteBackDestination.RTypeDestination
        private var aluOperator = ALUOperator.Add
        private var aluSource = ALUSource.ReadPortTwoOFRF
        private var nextPC = 0
        private var readPortOneOfRF:Int = 0
        private var readPortTwoOfRF:Int = 0
        private var instructionImmediateSection:Int = 0
        private var ITypeDestination = 0
        private var RTypeDestination = 0
    }

    fun storeOperands(operandOne:Int, operandTwo:Int){
        readPortOneOfRF = operandOne
        readPortTwoOfRF = operandTwo
    }

    fun storeImmediate(data:Int) {
        instructionImmediateSection = data
    }

    fun storeITypeDestination(data:Int){
        ITypeDestination = data
    }

    fun storeRTypeDestination(data:Int){
        RTypeDestination = data
    }

    fun storeNextPC(pc:Int){
        nextPC = pc
    }

    fun storeALUSource(source: ALUSource){
        aluSource = source
    }

    fun storeALUOperator(operator:ALUOperator){
        aluOperator = operator
    }

    fun storeWriteBackDestination(destination:WriteBackDestination){
        writeBackDestination = destination
    }

    fun storeIsBranchFlag(itIs:Boolean){
        isBranch = itIs
    }

    fun storeMemWriteFlag(write:Boolean){
        memoryWrite = write
    }

    fun storeMemReadFlag(read:Boolean){
        memoryRead = read
    }

    fun storeWritingOnRegisterFlag(write:Boolean){
        registerWrite = write
    }
}


package pipline_registers

import model.ALUOperator
import model.ALUSource
import model.RFWritePortSource
import model.WriteBackDestination
import java.util.*

class IDEXRegister {

    companion object {
        private var registerWrite = false
        private var memoryWrite = false
        private var memoryRead = false
        private var isBranch = false
        private var writeBackDestination = WriteBackDestination.RTypeDestination
        private var aluOperator = ALUOperator.Add
        private var aluSource = ALUSource.ReadPortTwoOFRF
        private var nextPC = 0
        private var readPortOneOfRF: Int = 0
        private var readPortTwoOfRF: Int = 0
        private var instructionImmediateSection: Int = 0
        private var ITypeDestination = 0
        private var RTypeDestination = 0
        private var rfWritePortSource = RFWritePortSource.AluResult
        private var instruction:BitSet = BitSet(32)
    }

    fun storeOperands(operandOne: Int, operandTwo: Int) {
        readPortOneOfRF = operandOne
        readPortTwoOfRF = operandTwo
    }

    fun storeImmediate(data: Int) {
        instructionImmediateSection = data
    }

    fun storeITypeDestination(data: Int) {
        ITypeDestination = data
    }

    fun storeRTypeDestination(data: Int) {
        RTypeDestination = data
    }

    fun storeNextPC(pc: Int) {
        nextPC = pc
    }

    fun storeALUSource(source: ALUSource) {
        aluSource = source
    }

    fun storeALUOperator(operator: ALUOperator) {
        aluOperator = operator
    }

    fun storeWriteBackDestination(destination: WriteBackDestination) {
        writeBackDestination = destination
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

    fun getReadPortOneDataOfRF() = readPortOneOfRF

    fun getReadPortTwoDataOfRF() = readPortTwoOfRF

    fun getImmediateData() = instructionImmediateSection

    fun getAluSource() = aluSource

    fun getALUOperator() = aluOperator

    fun getRegisterDestination() = writeBackDestination

    fun getRTypeDestination() = RTypeDestination

    fun getITypeDestination() = ITypeDestination

    fun getNextPC() = nextPC

    fun getIsBranchFlag() = isBranch

    fun getMemWriteFlag() = memoryWrite

    fun getMemReadFlag() = memoryRead

    fun getWritinOnRFFlag() = registerWrite

    fun storeRegisterWritePortSource(source: RFWritePortSource) {
        rfWritePortSource = source
    }

    fun getWritePortSource() = rfWritePortSource

    fun storeInstruction(inst:BitSet){
        instruction = inst
    }

    fun getInstruction() = instruction
}


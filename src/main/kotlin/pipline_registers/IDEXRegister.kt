package pipline_registers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.ALUOperator
import model.ALUSource
import model.InstructionModel
import model.RFWritePortSource
import stageExecute
import utils.colored
import utils.stallInstruction

class IDEXRegister {

    companion object {
        private var registerWrite_IN = false
        private var memoryWrite_IN = false
        private var memoryRead_IN = false
        private var isBranch_IN = false
        private var aluOperator_IN = ALUOperator.Add
        private var aluSource_IN = ALUSource.ReadPortTwoOFRF
        private var nextPC_IN = 0
        private var readPortOneOfRF_IN: Int = 0
        private var readPortTwoOfRF_IN: Int = 0
        private var instructionImmediateSection_IN: Int = 0
        private var rfWriteAddress_IN = 0
        private var rfWritePortSource_IN = RFWritePortSource.AluResult
        private var instruction_IN = stallInstruction
        private var stallSignal_IN = false

        private var registerWrite_OUT = false
        private var memoryWrite_OUT = false
        private var memoryRead_OUT = false
        private var isBranch_OUT = false
        private var aluOperator_OUT = ALUOperator.Add
        private var aluSource_OUT = ALUSource.ReadPortTwoOFRF
        private var nextPC_OUT = 0
        private var readPortOneOfRF_OUT: Int = 0
        private var readPortTwoOfRF_OUT: Int = 0
        private var instructionImmediateSection_OUT: Int = 0
        private var rfWriteAddress_OUT = 0
        private var rfWritePortSource_OUT = RFWritePortSource.AluResult
        private var instruction_OUT = stallInstruction
        private var stallSignal_OUT = true
    }

    fun activateRegister(clock: StateFlow<Int>) {
        CoroutineScope(Dispatchers.IO).launch {
            clock.collect {i ->
                copyInputToOutPut(i)

                stageExecute.executeInstruction(i)
            }
        }
    }

    private fun copyInputToOutPut(clock:Int) {
        registerWrite_OUT = registerWrite_IN
        memoryWrite_OUT = memoryWrite_IN
        memoryRead_OUT = memoryRead_IN
        isBranch_OUT = isBranch_IN
        aluOperator_OUT = aluOperator_IN
        aluSource_OUT = aluSource_IN
        nextPC_OUT = nextPC_IN
        readPortOneOfRF_OUT = readPortOneOfRF_IN
        readPortTwoOfRF_OUT = readPortTwoOfRF_IN
        instructionImmediateSection_OUT = instructionImmediateSection_IN
        rfWriteAddress_OUT = rfWriteAddress_IN
        rfWritePortSource_OUT = rfWritePortSource_IN
        instruction_OUT = instruction_IN
        stallSignal_OUT = stallSignal_IN

        colored {
            println("ID/EX on clock $clock ; instIN:${instruction_IN.id}".bold)
        }
    }

    fun storeOperands(operandOne: Int, operandTwo: Int) {
        readPortOneOfRF_IN = operandOne
        readPortTwoOfRF_IN = operandTwo
    }

    fun storeImmediate(data: Int) {
        instructionImmediateSection_IN = data
    }

    fun storeNextPC(pc: Int) {
        nextPC_IN = pc
    }

    fun storeALUSource(source: ALUSource) {
        aluSource_IN = source
    }

    fun storeALUOperator(operator: ALUOperator) {
        aluOperator_IN = operator
    }

    fun storeRFWriteAddress(destination: Int) {
        rfWriteAddress_IN = destination
    }

    fun storeIsBranchFlag(itIs: Boolean) {
        isBranch_IN = itIs
    }

    fun storeMemWriteFlag(write: Boolean) {
        memoryWrite_IN = write
    }

    fun storeMemReadFlag(read: Boolean) {
        memoryRead_IN = read
    }

    fun storeWritingOnRegisterFlag(write: Boolean) {
        registerWrite_IN = write
    }

    fun getReadPortOneDataOfRF() = readPortOneOfRF_OUT

    fun getReadPortTwoDataOfRF() = readPortTwoOfRF_OUT

    fun getImmediateData() = instructionImmediateSection_OUT

    fun getAluSource() = aluSource_OUT

    fun getALUOperator() = aluOperator_OUT

    fun getRFWriteAddress() = rfWriteAddress_OUT

    fun getNextPC() = nextPC_OUT

    fun getIsBranchFlag() = isBranch_OUT

    fun getMemWriteFlag() = memoryWrite_OUT

    fun getMemReadFlag() = memoryRead_OUT

    fun getWritinOnRFFlag() = registerWrite_OUT

    fun storeRegisterWritePortSource(source: RFWritePortSource) {
        rfWritePortSource_IN = source
    }

    fun getWritePortSource() = rfWritePortSource_OUT

    fun storeInstruction(inst: InstructionModel) {
        instruction_IN = inst
    }

    fun getInstruction() = instruction_OUT

    fun storeStallSignal(stall: Boolean) {
        stallSignal_IN = stall
    }

    fun getStallSignal() = stallSignal_OUT
}


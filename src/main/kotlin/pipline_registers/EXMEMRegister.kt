package pipline_registers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.InstructionModel
import model.PCSource
import model.RFWritePortSource
import stageMemory
import utils.colored
import utils.stallInstruction

class EXMEMRegister {

    companion object {
        private var registerWrite_IN = false
        private var memoryWrite_IN = false
        private var memoryRead_IN = false
        private var pcSource_IN = PCSource.NextPC
        private var aluResult_IN = 0
        private var readPortTwoOfRF_IN = 0
        private var rfWriteAddress_IN = 0
        private var rfWritePortSource_IN = RFWritePortSource.AluResult
        private var instruction_IN = stallInstruction
        private var stallSignal_IN = false
        private var endSignal_IN = false
        private var branchTarget_IN = 0
        private var isBranch_IN = false
        private var branchIsTook_IN = false

        private var registerWrite_OUT = false
        private var memoryWrite_OUT = false
        private var memoryRead_OUT = false
        private var pcSource_OUT = PCSource.NextPC
        private var aluResult_OUT = 0
        private var readPortTwoOfRF_OUT = 0
        private var rfWriteAddress_OUT = 0
        private var rfWritePortSource_OUT = RFWritePortSource.AluResult
        private var instruction_OUT = stallInstruction
        private var stallSignal_OUT = false
        private var endSignal_OUT = false
        private var branchTarget_OUT = 0
        private var isBranch_OUT = false
        private var branchIsTook_OUT = false
    }

    fun activateRegister(clock: StateFlow<Int>, shutDownClock: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            clock.collect { i ->
                copyInputToOutPut(i)

                stageMemory.applyMemWork(i, shutDownClock)
            }
        }
    }

    private fun copyInputToOutPut(clock: Int) {
        registerWrite_OUT = registerWrite_IN
        memoryWrite_OUT = memoryWrite_IN
        memoryRead_OUT = memoryRead_IN
        pcSource_OUT = pcSource_IN
        aluResult_OUT = aluResult_IN
        readPortTwoOfRF_OUT = readPortTwoOfRF_IN
        rfWriteAddress_OUT = rfWriteAddress_IN
        rfWritePortSource_OUT = rfWritePortSource_IN
        instruction_OUT = instruction_IN
        stallSignal_OUT = stallSignal_IN
        endSignal_OUT = endSignal_IN
        branchTarget_OUT = branchTarget_IN
        isBranch_OUT = isBranch_IN
        branchIsTook_OUT = branchIsTook_IN
        colored {
            println("EX/MEM on clock $clock ; instIN:${instruction_IN.id}".bold)
        }
    }

    fun storeALUResult(data: Int) {
        aluResult_IN = data
    }

    fun storeReadPortTwoData(data: Int) {
        readPortTwoOfRF_IN = data
    }

    fun storeRFWriteAddress(data: Int) {
        rfWriteAddress_IN = data
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

    fun getMemWriteFlag() = memoryWrite_OUT

    fun getALUResult() = aluResult_OUT

    fun getMemWriteData() = readPortTwoOfRF_OUT

    fun getMemReadFlag() = memoryRead_OUT

    fun getRFWriteAddress() = rfWriteAddress_OUT

    fun getRegisterWriteFalg() = registerWrite_OUT

    fun storeRegisterWritePortSource(source: RFWritePortSource) {
        rfWritePortSource_IN = source
    }

    fun getWritePortSource() = rfWritePortSource_OUT

    fun storeInstruction(inst: InstructionModel) {
        instruction_IN = inst
    }

    fun getInstruction() = instruction_OUT

    fun storeStallSignal(stallSignal: Boolean) {
        stallSignal_IN = stallSignal
    }

    fun getStallSignal() = stallSignal_OUT

    fun storeEndSignal(end: Boolean) {
        endSignal_IN = end
    }

    fun getEndSignal() = endSignal_OUT

    fun storePCSource(source: PCSource) {
        pcSource_IN = source
    }

    fun getPCSource() = pcSource_OUT

    fun storeBranchTarget(branchTarget: Int) {
        branchTarget_IN = branchTarget
    }

    fun getBranchTarget() = branchTarget_OUT

    fun storeIsBranchFlag(itIs:Boolean){
        isBranch_IN = itIs
    }

    fun getIsBranchFlag() = isBranch_OUT

    fun storeBranchIsTookFlag(took:Boolean){
        branchIsTook_IN = took
    }

    fun getBranchIsTook() = branchIsTook_OUT
}






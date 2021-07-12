package pipline_registers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.InstructionModel
import model.RFWritePortSource
import utils.colored
import utils.stallInstruction

class MEMWBRegister {

    companion object {
        private var registerWrite_IN = false
        private var dataMemoryOutPut_IN: Int = 0
        private var aluResult_IN: Int = 0
        private var rfWriteAddress_IN = 0
        private var rfWritePortSource_IN = RFWritePortSource.AluResult
        private var instruction_IN = stallInstruction

        private var registerWrite_OUT = false
        private var dataMemoryOutPut_OUT: Int = 0
        private var aluResult_OUT: Int = 0
        private var rfWriteAddress_OUT = 0
        private var rfWritePortSource_OUT = RFWritePortSource.AluResult
        private var instruction_OUT = stallInstruction
    }

    fun activateRegister(clock: StateFlow<Int>) {
        CoroutineScope(Dispatchers.IO).launch {
            clock.collect { i ->
                copyInputToOutPut(i)
            }
        }
    }

    private fun copyInputToOutPut(clock:Int) {
        registerWrite_OUT = registerWrite_IN
        dataMemoryOutPut_OUT = dataMemoryOutPut_IN
        aluResult_OUT = aluResult_IN
        rfWriteAddress_OUT = rfWriteAddress_IN
        rfWritePortSource_OUT = rfWritePortSource_IN
        instruction_OUT = instruction_IN

        colored {
            println("MEM/WB on clock $clock ; instIN:${instruction_IN.id}".bold)
        }
    }

    fun getWritingOnRegisterFlag(): Boolean = registerWrite_OUT

    fun getRFWriteAddress() = rfWriteAddress_OUT

    fun storeDataMemOutPut(data: Int) {
        dataMemoryOutPut_IN = data
    }

    fun storeRFWriteAddress(address: Int) {
        rfWriteAddress_IN = address
    }

    fun storeALUResult(result: Int) {
        aluResult_IN = result
    }

    fun storeRegisterWriteFlag(store: Boolean) {
        registerWrite_IN = store
    }

    fun storeRFStorePortSource(source: RFWritePortSource) {
        rfWritePortSource_IN = source
    }

    fun getRFWritePortSource() = rfWritePortSource_OUT

    fun getALUResult() = aluResult_OUT

    fun getDataMemOutPut() = dataMemoryOutPut_OUT

    fun storeInstruction(inst: InstructionModel) {
        instruction_IN = inst
    }

    fun getInstruction() = instruction_OUT

}


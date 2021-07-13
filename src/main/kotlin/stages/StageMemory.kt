package stages

import ex_mem
import mem_wb
import utils.*

class StageMemory {

    companion object {
        private val dataMemory = mutableListOf<Int>()
    }

    fun applyMemWork(clock: Int) {
        write(clock)
        read(clock)
        fillMEMWBRegister()
    }

    private fun fillMEMWBRegister() {
        //storing register file write address
        val rfWriteAddress = ex_mem.getRFWriteAddress()
        mem_wb.storeRFWriteAddress(rfWriteAddress)
        //storing ALU result
        val aluResult = ex_mem.getALUResult()
        mem_wb.storeALUResult(aluResult)
        //storing register write flag
        val registerStore = ex_mem.getRegisterWriteFalg()
        mem_wb.storeRegisterWriteFlag(registerStore)
        //storing register file write port source
        val writePortSource = ex_mem.getWritePortSource()
        mem_wb.storeRFStorePortSource(writePortSource)
        //storing instruction
        val instruction = ex_mem.getInstruction()
        mem_wb.storeInstruction(instruction)
    }

    private fun read(clock: Int) {
        val readFlag = ex_mem.getMemReadFlag()
        if (readFlag) {
            val memAddress = ex_mem.getALUResult()
            val data = dataMemory[memAddress]
            colored {
                println("MEM[$memAddress]=>$data on clock:$clock ; inst:${ex_mem.getInstruction().id}".yellow.bold)
            }
            mem_wb.storeDataMemOutPut(data)
        }
    }

    private fun write(clock: Int) {
        val writeFlag = ex_mem.getMemWriteFlag()
        if (writeFlag) {
            val memAddress = ex_mem.getALUResult()
            val data = ex_mem.getMemWriteData()
            colored {
                println("MEM[$memAddress]<=$data on clock:$clock ; inst:${ex_mem.getInstruction().id}".purple.bold)
            }
            if (memAddress > dataMemory.size - 1)
                dataMemory.add(memAddress, data)
            else
                dataMemory[memAddress] = data
        }
    }

    fun loadDataMemory(vararg datas: Int) {
        dataMemory.clear()

        datas.forEach { data ->
            dataMemory.add(data)
        }
    }

    fun readDataMEM(address: Int) = dataMemory[address]

}
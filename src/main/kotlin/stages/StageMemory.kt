package stages

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import pipline_registers.EXMEMRegister
import pipline_registers.MEMWBRegister
import utils.colored

class StageMemory {

    private val eXMEMRegister = EXMEMRegister()
    private val mEMWBRegister = MEMWBRegister()

    companion object {
        private val dataMemory = mutableListOf<Int>()
    }

    suspend fun activate(clock: StateFlow<Int>) {
        clock.collect { i ->
            applyMemWork(i)
        }
    }

    private fun applyMemWork(clock: Int) {
        write(clock)
        read(clock)
        fillMEMWBRegister()
    }

    private fun fillMEMWBRegister() {
        //storing register file write address
        val rfWriteAddress = eXMEMRegister.getRFWriteAddress()
        mEMWBRegister.storeRFWriteAddress(rfWriteAddress)
        //storing ALU result
        val aluResult = eXMEMRegister.getALUResult()
        mEMWBRegister.storeALUResult(aluResult)
        //storing register write flag
        val registerStore = eXMEMRegister.getRegisterWriteFalg()
        mEMWBRegister.storeRegisterWriteFlag(registerStore)
        //storing register file write port source
        val writePortSource = eXMEMRegister.getWritePortSource()
        mEMWBRegister.storeRFStorePortSource(writePortSource)
        //storing instruction
        val instruction = eXMEMRegister.getInstruction()
        mEMWBRegister.storeInstruction(instruction)
    }

    private fun read(clock:Int) {
        val readFlag = eXMEMRegister.getMemReadFlag()
        if (readFlag) {
            val memAddress = eXMEMRegister.getALUResult()
            val data = dataMemory[memAddress]
            colored {
                println("MEM[$memAddress]=>$data on clock:$clock ; inst:${eXMEMRegister.getInstruction().id}".yellow.bold)
            }
            mEMWBRegister.storeDataMemOutPut(data)
        }
    }

    private fun write(clock:Int) {
        val writeFlag = eXMEMRegister.getMemWriteFlag()
        if (writeFlag) {
            val memAddress = eXMEMRegister.getALUResult()
            val data = eXMEMRegister.getMemWriteData()
            colored {
                println("MEM[$memAddress]<=$data on clock:$clock ; inst:${eXMEMRegister.getInstruction().id}".purple.bold)
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
package stages

import pipline_registers.EXMEMRegister
import pipline_registers.MEMWBRegister

class StageMemory {

    private val eXMEMRegister = EXMEMRegister()
    private val mEMWBRegister = MEMWBRegister()

    companion object {
        private val dataMemory = mutableListOf<Int>()
    }

    fun applyMemWork() {
        write()
        read()
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

    private fun read() {
        val readFlag = eXMEMRegister.getMemReadFlag()
        if (readFlag) {
            val memAddress = eXMEMRegister.getALUResult()
            val data = dataMemory[memAddress]

            mEMWBRegister.storeDataMemOutPut(data)
        }
    }

    private fun write() {
        val writeFlag = eXMEMRegister.getMemWriteFlag()
        if (writeFlag) {
            val memAddress = eXMEMRegister.getALUResult()
            val data = eXMEMRegister.getMemWriteData()
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
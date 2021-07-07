package stages

import model.PCSource
import pipline_registers.EXMEMRegister
import pipline_registers.MEMWBRegister
import javax.inject.Inject

class StageMemory @Inject constructor() {

    @Inject
    lateinit var eXMEMRegister: EXMEMRegister

    @Inject
    lateinit var mEMWBRegister:MEMWBRegister

    companion object {
        private val dataMemory = mutableListOf<Int>()
    }

    fun applyMemWork() {
        write()
        read()
        fillMEMWBRegister()
    }

    private fun fillMEMWBRegister() {
        //storing register destination
        val registerDestination = eXMEMRegister.getReigsterDestination()
        mEMWBRegister.storeRegisterDestination(registerDestination)
        //storing ALU result
        val aluResult = eXMEMRegister.getALUResult()
        mEMWBRegister.storeALUResult(aluResult)
        //storing register write flag
        val registerStore = eXMEMRegister.getRegisterWriteFalg()
        mEMWBRegister.storeRegisterWriteFlag(registerStore)
        //storing register file write port source
        val writePortSource = eXMEMRegister.getWritePortSource()
        mEMWBRegister.storeRFStorePortSource(writePortSource)
    }

    private fun read() {
        val readFlag = eXMEMRegister.getMemReadFlag()
        if(readFlag){
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

            dataMemory[memAddress] = data
        }
    }

}
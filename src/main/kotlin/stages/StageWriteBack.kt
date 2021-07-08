package stages

import model.RFWritePortSource
import pipline_registers.MEMWBRegister

class StageWriteBack {

    private val mEMWBRegister = MEMWBRegister()

    fun getWriteBackData(): Int {
        val registerWriteSource = mEMWBRegister.getRFStorePortSource()
        return when (registerWriteSource) {
            RFWritePortSource.AluResult -> mEMWBRegister.getALUResult()
            RFWritePortSource.DataMemoryOutPut -> mEMWBRegister.getDataMemOutPut()
        }
    }

}
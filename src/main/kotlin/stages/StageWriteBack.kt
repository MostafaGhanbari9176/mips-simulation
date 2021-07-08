package stages

import model.RFWritePortSource
import pipline_registers.MEMWBRegister
import javax.inject.Inject

class StageWriteBack @Inject constructor() {

    @Inject
    lateinit var mEMWBRegister: MEMWBRegister

    fun getWriteBackData(): Int {
        val registerWriteSource = mEMWBRegister.getRFStorePortSource()
        return when (registerWriteSource) {
            RFWritePortSource.AluResult -> mEMWBRegister.getALUResult()
            RFWritePortSource.DataMemoryOutPut -> mEMWBRegister.getDataMemOutPut()
        }
    }

}
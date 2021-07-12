package stages

import mem_wb
import model.RFWritePortSource

class StageWriteBack {

    fun getWriteBackData(): Int {
        val registerWriteSource = mem_wb.getRFWritePortSource()
        return when (registerWriteSource) {
            RFWritePortSource.AluResult -> mem_wb.getALUResult()
            RFWritePortSource.DataMemoryOutPut -> mem_wb.getDataMemOutPut()
        }
    }

}
package stages

import id_ex
import if_id
import mem_wb
import model.*
import stageFetch
import stageWriteBack
import utils.*

class StageDecode {

    companion object {
        private val registerFile = MutableList<RegisterFileModel>(32) {
            RegisterFileModel()
        }

        private var instruction = stallInstruction
    }

    fun decodeInstruction(clock: Int) {
        //reading instruction from pipeline register(IF/ID)
        instruction = if_id.getInstruction()
        colored {
            println("decode instruction:${instruction.id} on clock:$clock".blue.bold)
            println("inst:${instruction.id} clock:$clock pending registers:${registerFile.mapIndexed { index, model -> object {val pending = model.pending; val display = "$index : ${model.pendingInstructionId}"}   }.filter { f-> f.pending }.map { g -> g.display }  }".blue.reverse.bold)
        }
        //separate operands address from instruction
        val readPortOneAddress = instruction.inst.substring(21, 26)
        val readPortTwoAddress = instruction.inst.substring(16, 21)
        //fetching operands value from register file
        val registerOne = registerFile[convertBinaryStringToUInt(readPortOneAddress)]
        val registerTwo = registerFile[convertBinaryStringToUInt(readPortTwoAddress)]
        //separate op code
        val opCode = instruction.inst.substring(26, 32)

        if (registerOne.pending || (registerTwo.pending && (opCode == "000000" || opCode == "101011"))) {
            colored {
                println("inject stall for instruction: ${instruction.id} clock:$clock".bold.reverse)
            }
            stageFetch.disablePC(true)
            if_id.disable(true)
            id_ex.storeStallSignal(true)
            instruction = stallInstruction
        }else{
            id_ex.storeStallSignal(false)
            stageFetch.disablePC(false)
            if_id.disable(false)
        }

        //storing operands to pipeline register(ID/EX)
        id_ex.storeOperands(registerOne.data, registerTwo.data)

        //separate immediate value from instruction
        val _immediate = instruction.inst.substring(0, 16)
        val immediate = convertBinaryStringToInt(_immediate)
        //storing immediate value to pipeline register(ID/EX)
        id_ex.storeImmediate(immediate)

        id_ex.storeRFWriteAddress(specifyRFWriteAddress())

        fillIDEXRegister()

        writeToRegister(clock)
    }

    private fun specifyRFWriteAddress(): Int {
        //separate op code
        val opCode = instruction.inst.substring(26, 32)

        val thisWriteOnRF = opCode != "000010" && opCode != "101011" && instruction.id != -1
        //specify writing instructions
        id_ex.storeWritingOnRegisterFlag(thisWriteOnRF)

        if (!thisWriteOnRF)
            return 0

        //separate i type destination register address from instruction
        val _iTypeDestination = instruction.inst.substring(16, 21)
        val iTypeDestination = convertBinaryStringToUInt(_iTypeDestination)
        //separate r type destination register address from instruction
        val _rTypeDestination = instruction.inst.substring(11, 16)
        val rTypeDestination = convertBinaryStringToUInt(_rTypeDestination)

        val writeAddress = if (opCode == "000000")
            rTypeDestination
        else
            iTypeDestination

        val writeRegister = registerFile[writeAddress]
        writeRegister.pending = true
        writeRegister.pendingInstructionId = instruction.id

        return writeAddress
    }

    private fun writeToRegister(clock: Int) {
        val writeOnRegister = mem_wb.getWritingOnRegisterFlag()
        val stall = mem_wb.getStallSignal()
        if (writeOnRegister && !stall) {
            val data = stageWriteBack.getWriteBackData()
            val writeAddress = mem_wb.getRFWriteAddress()

            val instruction = mem_wb.getInstruction()

            val register = registerFile[writeAddress]
            register.data = data

            if (register.pendingInstructionId == instruction.id)
                register.pending = false

            colored {
                println("RF[$writeAddress]<=$data on clock:$clock ; inst:${instruction.id} ; pending:${register.pending}".cyan.bold)
            }
        }
    }

    private fun fillIDEXRegister() {
        val nextPC = if_id.getNextPC()
        id_ex.storeNextPC(nextPC)
        //separate op code
        val opCode = instruction.inst.substring(26, 32)
        //specify ALU source
        id_ex.storeALUSource(if (opCode == "000000") ALUSource.ReadPortTwoOFRF else ALUSource.Immediate)

        //specify ALU operator
        val functionCode = instruction.inst.substring(0, 6)
        when (functionCode) {
            "100000" ->
                id_ex.storeALUOperator(ALUOperator.Add)
            "100010" ->
                id_ex.storeALUOperator(ALUOperator.Sub)
            "100101" ->
                id_ex.storeALUOperator(ALUOperator.OR)
            "100100" ->
                id_ex.storeALUOperator(ALUOperator.And)
            "101010" ->
                id_ex.storeALUOperator(ALUOperator.SLT)
        }

        //specify branch instruction
        id_ex.storeIsBranchFlag(opCode == "000100" || opCode == "000101")
        //specify lw instruction
        id_ex.storeMemReadFlag(opCode == "100011")
        //specify sw instruction(stall instruction id is -1)
        id_ex.storeMemWriteFlag(opCode == "101011" && instruction.id != -1)
        //specify register write data source
        id_ex.storeRegisterWritePortSource(
            if (opCode == "000000")
                RFWritePortSource.AluResult
            else
                RFWritePortSource.DataMemoryOutPut
        )
        //store instruction
        id_ex.storeInstruction(instruction)
    }

}



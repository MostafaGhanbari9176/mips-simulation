package stages

import model.ALUOperator
import model.ALUSource
import model.RFWritePortSource
import pipline_registers.IDEXRegister
import pipline_registers.IFIDRegister
import pipline_registers.MEMWBRegister
import utils.convertBinaryStringToInt
import utils.convertBinaryStringToUInt
import utils.substring

class StageDecode {

    private val ifIDRegister = IFIDRegister()
    private val idEXRegister = IDEXRegister()
    private val memWBRegister = MEMWBRegister()
    private val stageWriteBack = StageWriteBack()

    companion object {
        private val registerFile = Array<Int>(32) { 0 }
    }

    fun decodeInstruction() {
        //reading instruction from pipeline register(IF/ID)
        val instruction = ifIDRegister.getInstruction()
        //separate operands address from instruction
        val readPortOneAddress = instruction.substring(21, 26)
        val readPortTwoAddress = instruction.substring(16, 21)
        //fetching operands value from register file
        val operandOne = registerFile[convertBinaryStringToUInt(readPortOneAddress)]
        val operandTwo = registerFile[convertBinaryStringToUInt(readPortTwoAddress)]
        //storing operands to pipeline register(ID/EX)
        idEXRegister.storeOperands(operandOne, operandTwo)

        //separate immediate value from instruction
        val _immediate = instruction.substring(0, 16)
        val immediate = convertBinaryStringToInt(_immediate)
        //storing immediate value to pipeline register(ID/EX)
        idEXRegister.storeImmediate(immediate)

        idEXRegister.storeRFWriteAddress(specifyRFWriteAddress(instruction))

        fillIDEXRegister(instruction)

        writeToRegister()
    }

    private fun specifyRFWriteAddress(instruction: String): Int {
        //separate i type destination register address from instruction
        val _iTypeDestination = instruction.substring(16, 21)
        val iTypeDestination = convertBinaryStringToUInt(_iTypeDestination)
        //separate r type destination register address from instruction
        val _rTypeDestination = instruction.substring(11, 16)
        val rTypeDestination = convertBinaryStringToUInt(_rTypeDestination)

        //separate op code
        val opCode = instruction.substring(26, 32)

        return if (opCode == "000000")
            rTypeDestination
        else
            iTypeDestination
    }

    private fun writeToRegister() {
        val writeOnRegister = memWBRegister.getWritingOnRegisterFlag()
        if (writeOnRegister) {
            val data = stageWriteBack.getWriteBackData()
            val registerDestination = memWBRegister.getRFWriteAddress()

            registerFile.set(registerDestination, data)
        }
    }

    private fun fillIDEXRegister(instruction: String) {
        val nextPC = ifIDRegister.getNextPC()
        idEXRegister.storeNextPC(nextPC)
        //separate op code
        val opCode = instruction.substring(26, 32)
        //specify ALU source
        idEXRegister.storeALUSource(if (opCode == "000000") ALUSource.ReadPortTwoOFRF else ALUSource.Immediate)

        //specify ALU operator
        val functionCode = instruction.substring(0, 6)
        when (functionCode) {
            "100000" ->
                idEXRegister.storeALUOperator(ALUOperator.Add)
            "100010" ->
                idEXRegister.storeALUOperator(ALUOperator.Sub)
            "100101" ->
                idEXRegister.storeALUOperator(ALUOperator.OR)
            "100100" ->
                idEXRegister.storeALUOperator(ALUOperator.And)
            "101010" ->
                idEXRegister.storeALUOperator(ALUOperator.SLT)
        }

        //specify branch instruction
        idEXRegister.storeIsBranchFlag(opCode == "000100" || opCode == "000101")
        //specify lw instruction
        idEXRegister.storeMemReadFlag(opCode == "100011")
        //specify sw instruction
        idEXRegister.storeMemWriteFlag(opCode == "101011")
        //specify writing instructions
        idEXRegister.storeWritingOnRegisterFlag(opCode != "000010" && opCode != "101011")
        //specify register write data source
        idEXRegister.storeRegisterWritePortSource(
            if (opCode == "000000")
                RFWritePortSource.AluResult
            else
                RFWritePortSource.DataMemoryOutPut
        )
        //store instruction
        val instruction = ifIDRegister.getInstruction()
        idEXRegister.storeInstruction(instruction)
    }

}



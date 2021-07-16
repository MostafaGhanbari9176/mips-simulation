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
        if (checkForLastInst()) {
            stageFetch.programIsEnd()
            id_ex.storeEndSignal(true)
            return
        }
        //reading instruction from pipeline register(IF/ID)
        instruction = if_id.getInstruction()
        colored {
            println("decode instruction:${instruction.id} on clock:$clock".blue.bold)
            println(
                "inst:${instruction.id} clock:$clock pending registers:${
                    registerFile.mapIndexed { index, model ->
                        object {
                            val pending = model.pending;
                            val display = "$index : ${model.pendingInstructionId}"
                        }
                    }.filter { f -> f.pending }.map { g -> g.display }
                }".blue.reverse.bold)
        }

        checkForJump(getOpCode())

        //separate operands address from instruction
        val readPortOneAddress = instruction.inst.substring(21, 26)
        val readPortTwoAddress = instruction.inst.substring(16, 21)
        //fetching operands value from register file
        val registerOne = registerFile[convertBinaryStringToUInt(readPortOneAddress)]
        val registerTwo = registerFile[convertBinaryStringToUInt(readPortTwoAddress)]

        checkForDataHazard(registerOne, registerTwo,getOpCode(), clock)

        checkForControlHazard(getOpCode(), clock)

        //storing operands to pipeline register(ID/EX)
        id_ex.storeOperands(registerOne.data, registerTwo.data)

        //separate immediate value from instruction
        val _immediate = instruction.inst.substring(0, 16)
        val immediate = convertBinaryStringToInt(_immediate)
        //storing immediate value to pipeline register(ID/EX)
        id_ex.storeImmediate(immediate)

        id_ex.storeRFWriteAddress(specifyRFWriteAddress())

        fillIDEXRegister()

        //writeToRegister(clock)
    }

    private fun checkForJump(opCode: String) {
        if (opCode == "000010") {
            val _jTypeImm = instruction.inst.substring(0, 26)
            val jTypeImm = convertBinaryStringToInt(_jTypeImm)

            val nextPC = if_id.getNextPC()

            val targetAddress = (jTypeImm.toUInt() or (nextPC.toUInt() and 0xf0000000.toUInt())).toInt()

            if_id.resetIFID()
            stageFetch.changePC(targetAddress)
            instruction = stallInstruction
        }
    }

    private fun checkForControlHazard(opCode: String, clock: Int) {
        val isBranch = (opCode == "000100" || opCode == "000101")
        id_ex.storeIsBranchFlag(isBranch)

        if (isBranch) {
            colored {
                println("Branch Detected(${if (opCode == "000101") "BNE" else "BEQ"}), PC is disable, IF/ID is reset clcok:$clock".bold)
            }
            stageFetch.disablePC(true, if_id.getInstruction().id)
            if_id.resetIFID()
        }

    }

    private fun checkForDataHazard(
        registerOne: RegisterFileModel,
        registerTwo: RegisterFileModel,
        opCode: String,
        clock: Int
    ) {
        if (registerOne.pending || (registerTwo.pending && (opCode == "000000" || opCode == "101011" || opCode == "000100" || opCode == "000101"))) {
            colored {
                println("(DH)inject stall for instruction: ${instruction.id} clock:$clock".bold.reverse)
            }
            stageFetch.disablePC(true, if_id.getInstruction().id)
            if_id.disable(true)
            id_ex.storeStallSignal(true)
            instruction = stallInstruction
        } else if (instruction.id != -1) {
            id_ex.storeStallSignal(false)
            if_id.disable(false)
            if (opCode != "000100" && opCode != "000101")
                stageFetch.disablePC(false, if_id.getInstruction().id)
        }
    }

    private fun checkForLastInst(): Boolean {
        val instruction = if_id.getInstruction().inst

        val isLastIns = instruction.all { c -> c == '1' }

        return isLastIns
    }

    private fun specifyRFWriteAddress(): Int {
        //separate op code
        val opCode = instruction.inst.substring(26, 32)

        val thisWriteOnRF =
            opCode != "000010" && opCode != "000101" && opCode != "000100" && opCode != "101011" && instruction.id != -1
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

    fun writeToRegister(clock: Int) {
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
        id_ex.storeALUSource(
            if (opCode == "000000" || opCode == "000100" || opCode == "000101")
                ALUSource.ReadPortTwoOFRF
            else
                ALUSource.Immediate
        )

        //specify ALU operator
        val functionCode = instruction.inst.substring(0, 6)
        when {
            functionCode == "100000" || opCode == "001000" ->
                id_ex.storeALUOperator(ALUOperator.Add)

            functionCode == "100010" ->
                id_ex.storeALUOperator(ALUOperator.Sub)

            functionCode == "100101" || opCode == "001101" ->
                id_ex.storeALUOperator(ALUOperator.OR)

            functionCode == "100100" || opCode == "001100" ->
                id_ex.storeALUOperator(ALUOperator.And)

            functionCode == "101010" || opCode == "001010" ->
                id_ex.storeALUOperator(ALUOperator.SLT)
        }

        //specify lw instruction
        id_ex.storeMemReadFlag(opCode == "100011")
        //specify sw instruction(stall instruction id is -1)
        id_ex.storeMemWriteFlag(opCode == "101011" && instruction.id != -1)
        //specify register write data source
        id_ex.storeRegisterWritePortSource(
            if (opCode == "100011")
                RFWritePortSource.DataMemoryOutPut
            else
                RFWritePortSource.AluResult
        )
        //store instruction
        id_ex.storeInstruction(instruction)
    }

    private fun getOpCode(): String {
        //separate op code
        val opCode = instruction.inst.substring(26, 32)

        return opCode
    }

}



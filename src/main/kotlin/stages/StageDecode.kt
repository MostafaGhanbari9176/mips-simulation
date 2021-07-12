package stages

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import model.*
import pipline_registers.IDEXRegister
import pipline_registers.IFIDRegister
import pipline_registers.MEMWBRegister
import utils.colored
import utils.convertBinaryStringToInt
import utils.convertBinaryStringToUInt
import utils.substring

class StageDecode {

    private val ifIDRegister = IFIDRegister()
    private val idEXRegister = IDEXRegister()
    private val memWBRegister = MEMWBRegister()
    private val stageWriteBack = StageWriteBack()
    private val stageFetch = StageFetch()

    companion object {
        private val registerFile = MutableList<RegisterFileModel>(32) {
            RegisterFileModel()
        }
    }

    suspend fun activate(clock: StateFlow<Int>) {
        clock.collect { i ->
            decodeInstruction(i)
        }
    }

    private fun decodeInstruction(clock: Int) {
        //reading instruction from pipeline register(IF/ID)
        val instruction = ifIDRegister.getInstruction()
        colored {
            println("decode instruction:${instruction.id} on clock:$clock".blue.bold)
        }
        //separate operands address from instruction
        val readPortOneAddress = instruction.inst.substring(21, 26)
        val readPortTwoAddress = instruction.inst.substring(16, 21)
        //fetching operands value from register file
        val registerOne = registerFile[convertBinaryStringToUInt(readPortOneAddress)]
        val registerTwo = registerFile[convertBinaryStringToUInt(readPortTwoAddress)]
        //storing operands to pipeline register(ID/EX)
        if (registerOne.pending || registerTwo.pending) {
            colored {
                println("inject stall for instruction: ${instruction.id}".bold.reverse)
            }
            stageFetch.injectStall()
        }

        idEXRegister.storeOperands(registerOne.data, registerTwo.data)

        //separate immediate value from instruction
        val _immediate = instruction.inst.substring(0, 16)
        val immediate = convertBinaryStringToInt(_immediate)
        //storing immediate value to pipeline register(ID/EX)
        idEXRegister.storeImmediate(immediate)

        idEXRegister.storeRFWriteAddress(specifyRFWriteAddress(instruction))

        fillIDEXRegister(instruction)

        writeToRegister(clock)
    }

    private fun specifyRFWriteAddress(instruction: InstructionModel): Int {
        //separate op code
        val opCode = instruction.inst.substring(26, 32)

        val thisWriteOnRF = opCode != "000010" && opCode != "101011" && instruction.id != -1
        //specify writing instructions
        idEXRegister.storeWritingOnRegisterFlag(thisWriteOnRF)

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
        val writeOnRegister = memWBRegister.getWritingOnRegisterFlag()
        if (writeOnRegister) {
            val data = stageWriteBack.getWriteBackData()
            val writeAddress = memWBRegister.getRFWriteAddress()

            val instruction = memWBRegister.getInstruction()

            val register = registerFile[writeAddress]
            register.data = data

            if (register.pendingInstructionId == instruction.id)
                register.pending = false

            colored {
                println("RF[$writeAddress]<=$data on clock:$clock ; inst:${instruction.id} ; pending:${register.pending}".cyan.bold)
            }
        }
    }

    private fun fillIDEXRegister(instruction: InstructionModel) {
        val nextPC = ifIDRegister.getNextPC()
        idEXRegister.storeNextPC(nextPC)
        //separate op code
        val opCode = instruction.inst.substring(26, 32)
        //specify ALU source
        idEXRegister.storeALUSource(if (opCode == "000000") ALUSource.ReadPortTwoOFRF else ALUSource.Immediate)

        //specify ALU operator
        val functionCode = instruction.inst.substring(0, 6)
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
        //specify sw instruction(stall instruction id is -1)
        idEXRegister.storeMemWriteFlag(opCode == "101011" && instruction.id != -1)
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



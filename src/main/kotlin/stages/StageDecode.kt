package stages

import model.ALUOperator
import model.ALUSource
import model.RFWritePortSource
import model.WriteBackDestination
import pipline_registers.IDEXRegister
import pipline_registers.IFIDRegister
import pipline_registers.MEMWBRegister
import utils.convertBytesToInt
import utils.convertBytesToUInt
import java.util.*
import javax.inject.Inject

class StageDecode {

    @Inject
    lateinit var ifIDRegister: IFIDRegister

    @Inject
    lateinit var idEXRegister: IDEXRegister

    @Inject
    lateinit var memWBRegister: MEMWBRegister

    @Inject
    lateinit var stageWriteBack: StageWriteBack

    companion object {
        private val registerFile = Array<Int>(32) { 0 }
    }

    fun decodeInstruction() {
        //reading instruction from pipeline register(IF/ID)
        val instruction = ifIDRegister.getInstruction()
        //separate operands address from instruction
        val readPortOneAddress = instruction[21, 26].toByteArray().toList()
        val readPortTwoAddress = instruction[16, 21].toByteArray().toList()
        //fetching operands value from register file
        val operandOne = registerFile[convertBytesToUInt(readPortOneAddress)]
        val operandTwo = registerFile[convertBytesToUInt(readPortTwoAddress)]
        //storing operands to pipeline register(ID/EX)
        idEXRegister.storeOperands(operandOne, operandTwo)

        //separate immediate value from instruction
        val _immediate = instruction[0, 16].toByteArray().toList()
        val immediate = convertBytesToInt(_immediate)
        //storing immediate value to pipeline register(ID/EX)
        idEXRegister.storeImmediate(immediate)

        //separate i type destination register address from instruction
        val _iTypeDestination = instruction[16, 21].toByteArray().toList()
        val iTypeDestination = convertBytesToUInt(_iTypeDestination)
        //storing i type destination address to pipeline register(ID/EX)
        idEXRegister.storeITypeDestination(iTypeDestination)

        //separate r type destination register address from instruction
        val _rTypeDestination = instruction[11, 16].toByteArray().toList()
        val rTypeDestination = convertBytesToUInt(_rTypeDestination)
        //storing i type destination address to pipeline register(ID/EX)
        idEXRegister.storeRTypeDestination(rTypeDestination)

        fillIDEXRegister(instruction)

        writeToRegister()
    }

    private fun writeToRegister() {
        val writeOnRegister = memWBRegister.getWritingOnRegisterFlag()
        if (writeOnRegister) {
            val data = stageWriteBack.getWriteBackData()
            val registerDestination = memWBRegister.getRegisterDestination()

            registerFile.set(registerDestination, data)
        }
    }

    private fun fillIDEXRegister(instruction: BitSet) {
        val nextPC = ifIDRegister.getNextPC()
        idEXRegister.storeNextPC(nextPC)
        //separate op code
        val _opCode = instruction[26, 32].toByteArray().toList()
        val opCode = convertBytesToInt(_opCode)
        if (opCode == 0) {
            //specify ALU source
            idEXRegister.storeALUSource(ALUSource.ReadPortTwoOFRF)
            //specify Register Destination
            idEXRegister.storeWriteBackDestination(WriteBackDestination.RTypeDestination)
        } else {
            //specify ALU source
            idEXRegister.storeALUSource(ALUSource.Immediate)
            //specify Register destination
            idEXRegister.storeWriteBackDestination(WriteBackDestination.ITypeDestination)
        }
        //specify ALU operator
        val functionCode = instruction[0, 6]
        when (functionCode) {
            BitSet(6).apply { set(5) } ->
                idEXRegister.storeALUOperator(ALUOperator.Add)
            BitSet(6).apply { set(5);set(1) } ->
                idEXRegister.storeALUOperator(ALUOperator.Sub)
            BitSet(6).apply { set(5);set(2);set(0) } ->
                idEXRegister.storeALUOperator(ALUOperator.OR)
            BitSet(6).apply { set(5);set(1);set(3) } ->
                idEXRegister.storeALUOperator(ALUOperator.And)
        }

        //specify branch instruction
        idEXRegister.storeIsBranchFlag(opCode == 4 || opCode == 5)
        //specify lw instruction
        idEXRegister.storeMemReadFlag(opCode == 0b100011)
        //specify sw instruction
        idEXRegister.storeMemWriteFlag(opCode == 0b101011)
        //specify writing instructions
        idEXRegister.storeWritingOnRegisterFlag(opCode != 0b000010)
        //specify register write data source
        idEXRegister.storeRegisterWritePortSource(
            if (opCode == 0)
                RFWritePortSource.AluResult
            else
                RFWritePortSource.DataMemoryOutPut
        )
    }

}



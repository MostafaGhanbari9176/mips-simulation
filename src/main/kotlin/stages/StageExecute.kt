package stages

import model.ALUOperator
import model.ALUSource
import pipline_registers.EXMEMRegister
import pipline_registers.IDEXRegister
import utils.convertBinaryStringToInt
import utils.substring

class StageExecute {

    private val iDEXRegister = IDEXRegister()
    private val eXMEMRegister = EXMEMRegister()

    private var operandOne = 0
    private var operandTwo = 0

    fun executeInstruction() {
        readOperands()
        checkInstructionType()
        generateZeroFlag()
        generateBranchAddress()
        fillExMEMRegister()
    }

    private fun checkInstructionType() {
        val instruction = iDEXRegister.getInstruction()
        //separate op code
        val _opCode = instruction.inst.substring(26, 32)
        val opCode = convertBinaryStringToInt(_opCode)

        if (opCode == 0)
            applyOperator()
        else
            generateMemoryAddress()
    }

    private fun generateMemoryAddress() {
        val base = operandOne
        val offset = iDEXRegister.getImmediateData()

        val address = base + offset

        eXMEMRegister.storeALUResult(address)
    }

    private fun generateBranchAddress() {
        val nextPc = iDEXRegister.getNextPC()
        val immediate = iDEXRegister.getImmediateData()
        val branchAddress = immediate * 4 + nextPc

        eXMEMRegister.storeBranchAddress(branchAddress)
    }

    private fun fillExMEMRegister() {
        //store read port two of register file
        val readPortTwoOfRFData = iDEXRegister.getReadPortTwoDataOfRF()
        eXMEMRegister.storeReadPortTwoData(readPortTwoOfRFData)
        //store register write address
        val rfWriteAddress = iDEXRegister.getRFWriteAddress()
        eXMEMRegister.storeRFWriteAddress(rfWriteAddress)
        //specify is branch flag
        val isBranch = iDEXRegister.getIsBranchFlag()
        eXMEMRegister.storeIsBranchFlag(isBranch)
        //specify memory write flag
        val memoryWrite = iDEXRegister.getMemWriteFlag()
        eXMEMRegister.storeMemWriteFlag(memoryWrite)
        //specify memory read flag
        val memoryRead = iDEXRegister.getMemReadFlag()
        eXMEMRegister.storeMemReadFlag(memoryRead)
        //specify writing on register flag
        val writingOnRegister = iDEXRegister.getWritinOnRFFlag()
        eXMEMRegister.storeWritingOnRegisterFlag(writingOnRegister)
        //specify write port source
        val writePortSource = iDEXRegister.getWritePortSource()
        eXMEMRegister.storeRegisterWritePortSource(writePortSource)
    }

    private fun generateZeroFlag() {
        val zeroFlag = (operandOne - operandTwo) == 0
        eXMEMRegister.storeZeroFlag(zeroFlag)
    }

    private fun applyOperator() {
        val functioCode = iDEXRegister.getALUOperator()

        val result = when (functioCode) {
            ALUOperator.Add -> operandOne + operandTwo
            ALUOperator.Sub -> operandOne - operandTwo
            ALUOperator.OR -> operandOne or operandTwo
            ALUOperator.And -> operandOne and operandTwo
            ALUOperator.SLT -> if (operandOne < operandTwo)
                1
            else
                0
        }

        eXMEMRegister.storeALUResult(result)
    }

    private fun readOperands() {
        operandOne = iDEXRegister.getReadPortOneDataOfRF()
        val aluSource = iDEXRegister.getAluSource()
        operandTwo = when (aluSource) {
            ALUSource.Immediate -> iDEXRegister.getImmediateData()
            ALUSource.ReadPortTwoOFRF -> iDEXRegister.getReadPortTwoDataOfRF()
        }
    }

}
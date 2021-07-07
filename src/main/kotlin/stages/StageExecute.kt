package stages

import model.ALUOperator
import model.ALUSource
import model.WriteBackDestination
import pipline_registers.EXMEMRegister
import pipline_registers.IDEXRegister
import javax.inject.Inject

class StageExecute @Inject constructor() {

    @Inject
    lateinit var iDEXRegister: IDEXRegister

    @Inject
    lateinit var eXMEMRegister: EXMEMRegister

    private var operandOne = 0
    private var operandTwo = 0

    fun executeInstruction() {
        readOperands()
        applyOperator()
        generateZeroFlag()
        generateBranchAddress()
        fillExMEMRegister()
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
        //specify register destination
        val registerDestination = iDEXRegister.getRegisterDestination()
        eXMEMRegister.storeRegisterDestination(
            when (registerDestination) {
                WriteBackDestination.RTypeDestination -> iDEXRegister.getRTypeDestination()
                WriteBackDestination.ITypeDestination -> iDEXRegister.getITypeDestination()
            }
        )
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
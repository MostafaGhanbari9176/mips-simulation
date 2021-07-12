package stages

import ex_mem
import id_ex
import model.ALUOperator
import model.ALUSource
import utils.colored
import utils.convertBinaryStringToInt

class StageExecute {

    private var operandOne = 0
    private var operandTwo = 0

    fun executeInstruction(clock:Int) {
        readOperands()
        checkInstructionType(clock)
        generateZeroFlag()
        generateBranchAddress()
        fillExMEMRegister()
    }

    private fun checkInstructionType(clock: Int) {
        val instruction = id_ex.getInstruction()
        colored {
            println("execute instruction:${instruction.id} on clock:$clock".green.bold)
        }
        //separate op code
        val _opCode = instruction.inst.substring(26, 32)
        val opCode = convertBinaryStringToInt(_opCode)

        if (opCode == 0)
            applyOperator()
        else
            generateMemoryAddress()

        //store instruction
        ex_mem.storeInstruction(instruction)
    }

    private fun generateMemoryAddress() {
        val base = operandOne
        val offset = id_ex.getImmediateData()

        val address = base + offset

        ex_mem.storeALUResult(address)
    }

    private fun generateBranchAddress() {
        val nextPc = id_ex.getNextPC()
        val immediate = id_ex.getImmediateData()
        val branchAddress = immediate * 4 + nextPc

        ex_mem.storeBranchAddress(branchAddress)
    }

    private fun fillExMEMRegister() {
        //store read port two of register file
        val readPortTwoOfRFData = id_ex.getReadPortTwoDataOfRF()
        ex_mem.storeReadPortTwoData(readPortTwoOfRFData)
        //store register write address
        val rfWriteAddress = id_ex.getRFWriteAddress()
        ex_mem.storeRFWriteAddress(rfWriteAddress)
        //specify is branch flag
        val isBranch = id_ex.getIsBranchFlag()
        ex_mem.storeIsBranchFlag(isBranch)
        //specify memory write flag
        val memoryWrite = id_ex.getMemWriteFlag()
        ex_mem.storeMemWriteFlag(memoryWrite)
        //specify memory read flag
        val memoryRead = id_ex.getMemReadFlag()
        ex_mem.storeMemReadFlag(memoryRead)
        //specify writing on register flag
        val writingOnRegister = id_ex.getWritinOnRFFlag()
        ex_mem.storeWritingOnRegisterFlag(writingOnRegister)
        //specify write port source
        val writePortSource = id_ex.getWritePortSource()
        ex_mem.storeRegisterWritePortSource(writePortSource)
    }

    private fun generateZeroFlag() {
        val zeroFlag = (operandOne - operandTwo) == 0
        ex_mem.storeZeroFlag(zeroFlag)
    }

    private fun applyOperator() {
        val functioCode = id_ex.getALUOperator()

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

        ex_mem.storeALUResult(result)
    }

    private fun readOperands() {
        operandOne = id_ex.getReadPortOneDataOfRF()
        val aluSource = id_ex.getAluSource()
        operandTwo = when (aluSource) {
            ALUSource.Immediate -> id_ex.getImmediateData()
            ALUSource.ReadPortTwoOFRF -> id_ex.getReadPortTwoDataOfRF()
        }
    }

}
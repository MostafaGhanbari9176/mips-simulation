package stages

import ex_mem
import id_ex
import model.ALUOperator
import model.ALUSource
import model.PCSource
import utils.*

class StageExecute {

    private var operandOne = 0
    private var operandTwo = 0

    fun executeInstruction(clock: Int) {
        val programISEnd = id_ex.getEndSignal()
        ex_mem.storeEndSignal(programISEnd)
        if (programISEnd)
            return
        readOperands(clock)
        checkInstructionType(clock)
        checkForBranch()
        fillExMEMRegister()
    }

    private fun checkInstructionType(clock: Int) {
        val instruction = id_ex.getInstruction()
        colored {
            println("execute instruction:${instruction.id} on clock:$clock".green.bold)
        }
        //separate op code
        val opCode = instruction.inst.substring(26, 32)

        if (opCode == "101011" || opCode == "100011")
            generateMemoryAddress()
        else
            applyOperator(clock)

        //store instruction
        ex_mem.storeInstruction(instruction)
    }

    private fun generateMemoryAddress() {
        val base = operandOne
        val offset = id_ex.getImmediateData()

        val address = base + offset

        ex_mem.storeALUResult(address)
    }

    private fun checkForBranch() {
        ex_mem.storePCSource(PCSource.NextPC)

        if (id_ex.getIsBranchFlag()) {
            ex_mem.storeIsBranchFlag(true)
            val zeroFlag = ((operandOne - operandTwo) == 0)
            val opCode = id_ex.getInstruction().inst.substring(26, 32)

            if((zeroFlag && opCode == "000100") || (!zeroFlag && opCode == "000101")){
                ex_mem.storeBranchIsTookFlag(true)
                val nextPc = id_ex.getNextPC()
                val immediate = id_ex.getImmediateData()

                val branchTarget = immediate + nextPc

                ex_mem.storePCSource(PCSource.Branch)
                ex_mem.storeBranchTarget(branchTarget)
            }else
                ex_mem.storeBranchIsTookFlag(false)

        }else
            ex_mem.storeIsBranchFlag(false)
    }

    private fun fillExMEMRegister() {
        //store read port two of register file
        val readPortTwoOfRFData = id_ex.getReadPortTwoDataOfRF()
        ex_mem.storeReadPortTwoData(readPortTwoOfRFData)
        //store register write address
        val rfWriteAddress = id_ex.getRFWriteAddress()
        ex_mem.storeRFWriteAddress(rfWriteAddress)
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
        //store stall signal
        val stallSignal = id_ex.getStallSignal()
        ex_mem.storeStallSignal(stallSignal)
    }

    private fun applyOperator(clock: Int) {
        val function = id_ex.getALUOperator()

        val result = when (function) {
            ALUOperator.Add -> operandOne + operandTwo
            ALUOperator.Sub -> operandOne - operandTwo
            ALUOperator.OR -> operandOne or operandTwo
            ALUOperator.And -> operandOne and operandTwo
            ALUOperator.SLT -> if (operandOne < operandTwo)
                1
            else
                0
            else -> -11
        }
        colored {
            println("ALU Result:$result $operandOne${function.name}$operandTwo inst:${id_ex.getInstruction().id} clock:$clock".green.bold.reverse)
        }
        ex_mem.storeALUResult(result)
    }

    private fun readOperands(clock: Int) {
        operandOne = id_ex.getReadPortOneDataOfRF()
        val aluSource = id_ex.getAluSource()
        operandTwo = when (aluSource) {
            ALUSource.Immediate -> {
/*                colored{
                    println("inst:${id_ex.getInstruction().id} clock:$clock ALUI1:$operandOne ALUI2:${id_ex.getImmediateData()} IMM".purple.bold.reverse)
                }*/
                id_ex.getImmediateData()
            }
            ALUSource.ReadPortTwoOFRF -> {
/*                colored{
                    println("inst:${id_ex.getInstruction().id} clock:$clock ALUI1:$operandOne ALUI2:${id_ex.getReadPortTwoDataOfRF()} RF".purple.bold.reverse)
                }*/
                id_ex.getReadPortTwoDataOfRF()
            }
        }
    }

}
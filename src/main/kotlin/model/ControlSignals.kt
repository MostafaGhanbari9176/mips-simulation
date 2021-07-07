package model

enum class ALUSource{
    Immediate,
    ReadPortTwoOFRF
}

enum class WriteBackDestination{
    RTypeDestination,
    ITypeDestination
}

enum class ALUOperand{
    Add,
    Sub,
    OR,
    And
}

enum class RFWritePortSource{
    AluResult,
    DataMemoryOutPut
}

enum class PCSource{
    NextPC,
    Branch
}
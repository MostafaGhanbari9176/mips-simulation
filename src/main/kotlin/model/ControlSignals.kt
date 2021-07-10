package model

enum class ALUSource{
    Immediate,
    ReadPortTwoOFRF
}

enum class ALUOperator{
    Add,
    Sub,
    OR,
    And,
    SLT
}

enum class RFWritePortSource{
    AluResult,
    DataMemoryOutPut
}

enum class PCSource{
    NextPC,
    Branch
}
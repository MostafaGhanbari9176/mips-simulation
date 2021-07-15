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
    SLT,
    AddI,
    SltI,
    AndI,
    OrI,
    None
}

enum class RFWritePortSource{
    AluResult,
    DataMemoryOutPut
}

enum class PCSource{
    NextPC,
    Branch
}
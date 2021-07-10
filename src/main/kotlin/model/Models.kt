package model

data class RegisterFileModel(
    val data: Int = 0,
    val pendingInstructionId:Int = -1,
    var pending: Boolean = false
)

data class InstructionModel(
    val inst:String,
    val id:Int
)
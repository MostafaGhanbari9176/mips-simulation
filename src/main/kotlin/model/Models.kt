package model

data class RegisterFileModel(
    var data: Int = 0,
    var pendingInstructionId:Int = -1,
    var pending: Boolean = false
)

data class InstructionModel(
    val inst:String,
    val id:Int
)
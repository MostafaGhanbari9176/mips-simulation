package utils

import model.InstructionModel
import java.util.*
import kotlin.experimental.and

val stallInstruction = InstructionModel("00000000000000000000000000000000", -1)

/**
 * converting binary string to UInt
 *
 * @param [data] data for converting, msb byte must be store on index 0
 */
fun convertBinaryStringToUInt(data: String): Int {
    var result = 0

    data.forEach { c ->
        result = (result shl 1) or c.toString().toInt()
    }

    return result
}

/**
 * converting binary string to int
 *
 * @param [data] data for converting, msb byte must be store on index 0
 */
fun convertBinaryStringToInt(data: String): Int {
    var result = 0

    data.forEach { c ->
        result = (result shl 1) or c.toString().toInt()
    }

    //sign extend for negative values
    if (data[0] == '1') {
        var operator = Int.MAX_VALUE
        operator = operator shl data.length

        result = operator or result
    }

    return result
}

fun String.substring(s:Int, e:Int):String{
    return (this as java.lang.String).substring(this.length - e, this.length - s)
}


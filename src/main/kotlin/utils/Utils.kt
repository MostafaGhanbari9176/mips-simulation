package utils

import java.util.*
import kotlin.experimental.and

/**
 * converting byteArray to UInt
 *
 * @param [data] data for converting, msb byte must be store on index 0
 */
fun convertBytesToUInt(data: List<Byte>): Int {
    var result = 0

    data.forEach { b ->
        result = (result shl 8) or b.toUByte().toInt()
    }

    return result
}

/**
 * converting byteArray to int
 *
 * @param [data] data for converting, msb byte must be store on index 0
 */
fun convertBytesToInt(data: List<Byte>): Int {
    var result = 0

    data.forEach { b ->
        result = (result shl 8) or b.toUByte().toInt()
    }

    //sign extend for negative values
    if ((data[0] and 0x80.toByte()) == 0x80.toByte()) {
        var operator = Int.MAX_VALUE
        operator = operator shl 8 * data.size

        result = operator or result
    }

    return result
}
package com.lemon.mcdevmanagermp.utils

object SM4 {
    private const val ENCRYPT = 1
    private const val DECRYPT = 0
    private const val ROUND = 32
    private const val BLOCK = 16

    private val SboxTable = byteArrayOf(
        0xd6.toByte(), 0x90.toByte(), 0xe9.toByte(), 0xfe.toByte(), 0xcc.toByte(), 0xe1.toByte(), 0x3d.toByte(), 0xb7.toByte(), 0x16.toByte(), 0xb6.toByte(), 0x14.toByte(), 0xc2.toByte(), 0x28.toByte(), 0xfb.toByte(), 0x2c.toByte(), 0x05.toByte(),
        0x2b.toByte(), 0x67.toByte(), 0x9a.toByte(), 0x76.toByte(), 0x2a.toByte(), 0xbe.toByte(), 0x04.toByte(), 0xc3.toByte(), 0xaa.toByte(), 0x44.toByte(), 0x13.toByte(), 0x26.toByte(), 0x49.toByte(), 0x86.toByte(), 0x06.toByte(), 0x99.toByte(),
        0x9c.toByte(), 0x42.toByte(), 0x50.toByte(), 0xf4.toByte(), 0x91.toByte(), 0xef.toByte(), 0x98.toByte(), 0x7a.toByte(), 0x33.toByte(), 0x54.toByte(), 0x0b.toByte(), 0x43.toByte(), 0xed.toByte(), 0xcf.toByte(), 0xac.toByte(), 0x62.toByte(),
        0xe4.toByte(), 0xb3.toByte(), 0x1c.toByte(), 0xa9.toByte(), 0xc9.toByte(), 0x08.toByte(), 0xe8.toByte(), 0x95.toByte(), 0x80.toByte(), 0xdf.toByte(), 0x94.toByte(), 0xfa.toByte(), 0x75.toByte(), 0x8f.toByte(), 0x3f.toByte(), 0xa6.toByte(),
        0x47.toByte(), 0x07.toByte(), 0xa7.toByte(), 0xfc.toByte(), 0xf3.toByte(), 0x73.toByte(), 0x17.toByte(), 0xba.toByte(), 0x83.toByte(), 0x59.toByte(), 0x3c.toByte(), 0x19.toByte(), 0xe6.toByte(), 0x85.toByte(), 0x4f.toByte(), 0xa8.toByte(),
        0x68.toByte(), 0x6b.toByte(), 0x81.toByte(), 0xb2.toByte(), 0x71.toByte(), 0x64.toByte(), 0xda.toByte(), 0x8b.toByte(), 0xf8.toByte(), 0xeb.toByte(), 0x0f.toByte(), 0x4b.toByte(), 0x70.toByte(), 0x56.toByte(), 0x9d.toByte(), 0x35.toByte(),
        0x1e.toByte(), 0x24.toByte(), 0x0e.toByte(), 0x5e.toByte(), 0x63.toByte(), 0x58.toByte(), 0xd1.toByte(), 0xa2.toByte(), 0x25.toByte(), 0x22.toByte(), 0x7c.toByte(), 0x3b.toByte(), 0x01.toByte(), 0x21.toByte(), 0x78.toByte(), 0x87.toByte(),
        0xd4.toByte(), 0x00.toByte(), 0x46.toByte(), 0x57.toByte(), 0x9f.toByte(), 0xd3.toByte(), 0x27.toByte(), 0x52.toByte(), 0x4c.toByte(), 0x36.toByte(), 0x02.toByte(), 0xe7.toByte(), 0xa0.toByte(), 0xc4.toByte(), 0xc8.toByte(), 0x9e.toByte(),
        0xea.toByte(), 0xbf.toByte(), 0x8a.toByte(), 0xd2.toByte(), 0x40.toByte(), 0xc7.toByte(), 0x38.toByte(), 0xb5.toByte(), 0xa3.toByte(), 0xf7.toByte(), 0xf2.toByte(), 0xce.toByte(), 0xf9.toByte(), 0x61.toByte(), 0x15.toByte(), 0xa1.toByte(),
        0xe0.toByte(), 0xae.toByte(), 0x5d.toByte(), 0xa4.toByte(), 0x9b.toByte(), 0x34.toByte(), 0x1a.toByte(), 0x55.toByte(), 0xad.toByte(), 0x93.toByte(), 0x32.toByte(), 0x30.toByte(), 0xf5.toByte(), 0x8c.toByte(), 0xb1.toByte(), 0xe3.toByte(),
        0x1d.toByte(), 0xf6.toByte(), 0xe2.toByte(), 0x2e.toByte(), 0x82.toByte(), 0x66.toByte(), 0xca.toByte(), 0x60.toByte(), 0xc0.toByte(), 0x29.toByte(), 0x23.toByte(), 0xab.toByte(), 0x0d.toByte(), 0x53.toByte(), 0x4e.toByte(), 0x6f.toByte(),
        0xd5.toByte(), 0xdb.toByte(), 0x37.toByte(), 0x45.toByte(), 0xde.toByte(), 0xfd.toByte(), 0x8e.toByte(), 0x2f.toByte(), 0x03.toByte(), 0xff.toByte(), 0x6a.toByte(), 0x72.toByte(), 0x6d.toByte(), 0x6c.toByte(), 0x5b.toByte(), 0x51.toByte(),
        0x8d.toByte(), 0x1b.toByte(), 0xaf.toByte(), 0x92.toByte(), 0xbb.toByte(), 0xdd.toByte(), 0xbc.toByte(), 0x7f.toByte(), 0x11.toByte(), 0xd9.toByte(), 0x5c.toByte(), 0x41.toByte(), 0x1f.toByte(), 0x10.toByte(), 0x5a.toByte(), 0xd8.toByte(),
        0x0a.toByte(), 0xc1.toByte(), 0x31.toByte(), 0x88.toByte(), 0xa5.toByte(), 0xcd.toByte(), 0x7b.toByte(), 0xbd.toByte(), 0x2d.toByte(), 0x74.toByte(), 0xd0.toByte(), 0x12.toByte(), 0xb8.toByte(), 0xe5.toByte(), 0xb4.toByte(), 0xb0.toByte(),
        0x89.toByte(), 0x69.toByte(), 0x97.toByte(), 0x4a.toByte(), 0x0c.toByte(), 0x96.toByte(), 0x77.toByte(), 0x7e.toByte(), 0x65.toByte(), 0xb9.toByte(), 0xf1.toByte(), 0x09.toByte(), 0xc5.toByte(), 0x6e.toByte(), 0xc6.toByte(), 0x84.toByte(),
        0x18.toByte(), 0xf0.toByte(), 0x7d.toByte(), 0xec.toByte(), 0x3a.toByte(), 0xdc.toByte(), 0x4d.toByte(), 0x20.toByte(), 0x79.toByte(), 0xee.toByte(), 0x5f.toByte(), 0x3e.toByte(), 0xd7.toByte(), 0xcb.toByte(), 0x39.toByte(), 0x48.toByte()
    )

    private val FK = intArrayOf(0xa3b1bac6.toInt(), 0x56aa3350, 0x677d9197, 0xb27022dc.toInt())
    private val CK = intArrayOf(
        0x00070e15, 0x1c232a31, 0x383f464d, 0x545b6269,
        0x70777e85, 0x8c939aa1.toInt(), 0xa8afb6bd.toInt(), 0xc4cbd2d9.toInt(),
        0xe0e7eef5.toInt(), 0xfc030a11.toInt(), 0x181f262d, 0x343b4249,
        0x50575e65, 0x6c737a81, 0x888f969d.toInt(), 0xa4abb2b9.toInt(),
        0xc0c7ced5.toInt(), 0xdce3eaf1.toInt(), 0xf8ff060d.toInt(), 0x141b2229,
        0x30373e45, 0x4c535a61, 0x686f767d, 0x848b9299.toInt(),
        0xa0a7aeb5.toInt(), 0xbcc3cad1.toInt(), 0xd8dfe6ed.toInt(), 0xf4fb0209.toInt(),
        0x10171e25, 0x2c333a41, 0x484f565d, 0x646b7279
    )

    private fun rotl(x: Int, n: Int): Int {
        return (x shl n) or (x ushr (32 - n))
    }

    private fun byteSub(A: Int): Int {
        return ((SboxTable[(A ushr 24) and 0xFF].toInt() and 0xFF) shl 24) or
                ((SboxTable[(A ushr 16) and 0xFF].toInt() and 0xFF) shl 16) or
                ((SboxTable[(A ushr 8) and 0xFF].toInt() and 0xFF) shl 8) or
                (SboxTable[A and 0xFF].toInt() and 0xFF)
    }

    private fun l1(B: Int): Int {
        return B xor rotl(B, 2) xor rotl(B, 10) xor rotl(B, 18) xor rotl(B, 24)
    }

    private fun l2(B: Int): Int {
        return B xor rotl(B, 13) xor rotl(B, 23)
    }

    private fun sm4CalciRK(ka: IntArray): IntArray {
        val rk = IntArray(ROUND)
        val k = IntArray(4)
        for (i in 0..3) {
            k[i] = ka[i] xor FK[i]
        }
        for (i in 0 until ROUND) {
            rk[i] = k[i] xor l2(byteSub(k[(i + 1) % 4] xor k[(i + 2) % 4] xor k[(i + 3) % 4] xor CK[i]))
            k[i % 4] = rk[i]
        }
        return rk
    }

    private fun sm4OneRound(sk: IntArray, input: ByteArray, output: ByteArray) {
        var i = 0
        val ulbuf = IntArray(36)
        while (i < 4) {
            ulbuf[i] = ((input[i * 4].toInt() and 0xFF) shl 24) or
                    ((input[i * 4 + 1].toInt() and 0xFF) shl 16) or
                    ((input[i * 4 + 2].toInt() and 0xFF) shl 8) or
                    (input[i * 4 + 3].toInt() and 0xFF)
            i++
        }
        i = 0
        while (i < 32) {
            ulbuf[i + 4] = ulbuf[i] xor l1(byteSub(ulbuf[i + 1] xor ulbuf[i + 2] xor ulbuf[i + 3] xor sk[i]))
            i++
        }
        i = 0
        while (i < 4) {
            output[i * 4] = (ulbuf[35 - i] ushr 24).toByte()
            output[i * 4 + 1] = (ulbuf[35 - i] ushr 16).toByte()
            output[i * 4 + 2] = (ulbuf[35 - i] ushr 8).toByte()
            output[i * 4 + 3] = (ulbuf[35 - i]).toByte()
            i++
        }
    }

    private fun padding(input: ByteArray, mode: Int): ByteArray {
        var ret: ByteArray? = null
        if (mode == ENCRYPT) {
            val p = 16 - input.size % 16
            ret = ByteArray(input.size + p)
            input.copyInto(ret, 0, 0, input.size)
            for (i in 0 until p) {
                ret[input.size + i] = p.toByte()
            }
        } else {
            val p = input[input.size - 1].toInt()
            ret = ByteArray(input.size - p)
            input.copyInto(ret, 0, 0, input.size - p)
        }
        return ret
    }

    // Kotlin replacement for System.arraycopy
    private fun System_arraycopy(src: ByteArray, srcPos: Int, dest: ByteArray, destPos: Int, length: Int) {
        src.copyInto(dest, destPos, srcPos, srcPos + length)
    }

    fun encryptECB(input: ByteArray, key: ByteArray): ByteArray {
        val ctx = SM4Context()
        ctx.isPadding = true
        ctx.mode = ENCRYPT

        val keyInt = IntArray(4)
        for (i in 0..3) {
            keyInt[i] = ((key[i * 4].toInt() and 0xFF) shl 24) or
                    ((key[i * 4 + 1].toInt() and 0xFF) shl 16) or
                    ((key[i * 4 + 2].toInt() and 0xFF) shl 8) or
                    (key[i * 4 + 3].toInt() and 0xFF)
        }
        ctx.sk = sm4CalciRK(keyInt)

        val inputPadded = padding(input, ENCRYPT)
        val output = ByteArray(inputPadded.size)

        var i = 0
        while (i < inputPadded.size) {
            val inBlock = ByteArray(16)
            val outBlock = ByteArray(16)
            System_arraycopy(inputPadded, i, inBlock, 0, 16)
            sm4OneRound(ctx.sk, inBlock, outBlock)
            System_arraycopy(outBlock, 0, output, i, 16)
            i += 16
        }
        return output
    }

    class SM4Context {
        var mode: Int = 1
        var sk: IntArray = IntArray(32)
        var isPadding: Boolean = true
    }
}


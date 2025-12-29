package com.lemon.mcdevmanagermp.utils

import com.lemon.mcdevmanagermp.data.netease.login.PVInfo
import com.lemon.mcdevmanagermp.data.netease.login.PVResultStrBean
import org.bouncycastle.crypto.engines.SM4Engine
import org.bouncycastle.crypto.paddings.PKCS7Padding
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.util.encoders.Hex
import java.math.BigInteger
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Date
import javax.crypto.Cipher
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
actual fun rsaEncrypt(input: String, publicKeyStr: String): String {
    // 将公钥字符串转换为PublicKey对象
    val keyBytes = Base64.decode(publicKeyStr)
    val keySpec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    val publicKey = keyFactory.generatePublic(keySpec)

    // 使用公钥进行RSA加密
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    val cipherText = cipher.doFinal(input.toByteArray())

    // 将加密后的字节数组转换为字符串
    return Base64.encode(cipherText)
}

actual fun sm4Encrypt(input: String, key: String): String {
    val keyBytes = Hex.decode(key)
    val inputBytes = input.toByteArray(Charsets.UTF_8)

    val cipher = PaddedBufferedBlockCipher(SM4Engine(), PKCS7Padding())
    cipher.init(true, KeyParameter(keyBytes))

    val outputBytes = ByteArray(cipher.getOutputSize(inputBytes.size))
    val length1 = cipher.processBytes(inputBytes, 0, inputBytes.size, outputBytes, 0)
    val length2 = cipher.doFinal(outputBytes, length1)

    return Hex.toHexString(outputBytes, 0, length1 + length2)
}

actual suspend fun vdfAsync(data: PVInfo): PVResultStrBean {
    val puzzle = data.args.puzzle
    val mod = BigInteger(data.args.mod, 16)
    var x = BigInteger(data.args.x, 16)
    val t = data.args.t
    val startTime = Date().time
    var count = 0

    while (count < t || Date().time - startTime < data.minTime) {
        x = x.multiply(x).mod(mod)
        count++
        val nowTime = Date().time
        if (nowTime - startTime > data.maxTime) {
            break
        }
    }

    val time = Date().time - startTime
    val signObj = mapOf(
        "runTimes" to count.toUInt(),
        "spendTime" to time.toUInt(),
        "t" to count.toUInt(),
        "x" to x.toString(16)
    )

    val sortedParams = listOf("runTimes", "spendTime", "t", "x")
    val encodedParams = sortedParams.joinToString("&") { key ->
        val value = signObj[key].toString()
        "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
    }

    Logger.d("encodedParams: $encodedParams, count: ${count.toUInt()}")
    val sign = murmurHash3(encodedParams, count.toUInt())

    return PVResultStrBean(
        maxTime = data.maxTime,
        puzzle = puzzle,
        spendTime = time.toInt(),
        runTimes = count,
        sid = data.sid,
        args = """{"x":"${x.toString(16)}","t":$count,"sign":"$sign"}"""
    )
}

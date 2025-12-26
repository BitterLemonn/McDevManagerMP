package com.lemon.mcdevmanagermp.utils

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.lemon.mcdevmanagermp.data.netease.login.PVInfo
import com.lemon.mcdevmanagermp.data.netease.login.PVResultStrBean
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFErrorRef
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSMutableCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.timeIntervalSince1970
import platform.Security.SecKeyCreateEncryptedData
import platform.Security.SecKeyCreateWithData
import platform.Security.SecKeyRef
import platform.Security.kSecAttrKeyClass
import platform.Security.kSecAttrKeyClassPublic
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeRSA
import platform.Security.kSecKeyAlgorithmRSAEncryptionPKCS1

@OptIn(ExperimentalForeignApi::class)
actual fun rsaEncrypt(input: String, publicKeyStr: String): String {
    return memScoped {
        // 1. 将 Base64 公钥字符串转为 NSData
        val keyData = NSData.create(base64Encoding = publicKeyStr)
            ?: return "Error: Invalid Public Key Base64"

        // 2. 构造属性字典 (使用更稳健的 CF 类型转换)
        val keys = allocArrayOf(kSecAttrKeyType, kSecAttrKeyClass)
        val values = allocArrayOf(kSecAttrKeyTypeRSA, kSecAttrKeyClassPublic)

        val attributes: CFDictionaryRef = CFDictionaryCreate(
            null,
            keys.reinterpret(),
            values.reinterpret(),
            2,
            null,
            null
        ) ?: return "Error: Failed to create attributes"

        // 3. 生成 SecKeyRef
        // 注意：使用 CFDataRef 和 CFDictionaryRef 别名，避免出现 __CF 开头的内部类报错
        val secKey: SecKeyRef = SecKeyCreateWithData(
            keyData as CFDataRef,
            attributes,
            null
        ) ?: return "Error: Failed to create SecKey"

        // 4. 准备明文数据
        val inputData = (input as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            ?: return "Error: Encoding failed"

        // 5. 执行加密 (使用 PKCS1 填充)
        var error: CFErrorRef? = null
        val cipherDataRef = SecKeyCreateEncryptedData(
            secKey,
            kSecKeyAlgorithmRSAEncryptionPKCS1,
            inputData as CFDataRef,
            null // 如果需要调试错误，可以传入 error.ptr
        )

        if (cipherDataRef == null) {
            return "Error: Encryption execution failed"
        }

        // 6. 转换为 Base64 字符串
        val cipherData = cipherDataRef as NSData
        cipherData.base64EncodedStringWithOptions(0UL)
    }
}

actual fun sm4Encrypt(input: String, key: String): String {
    val keyBytes = key.hexToByteArray()
    val inputBytes = input.encodeToByteArray()

    // 假设你已经有了 SM4 的实现类
    val encrypted = SM4.encryptECB(keyBytes, inputBytes)

    return encrypted.toHexString()
}

actual suspend fun vdfAsync(data: PVInfo): PVResultStrBean {
    val puzzle = data.args.puzzle
    val mod = BigInteger.parseString(data.args.mod, 16)
    var x = BigInteger.parseString(data.args.x, 16)
    val t = data.args.t

    val startTime = NSDate().timeIntervalSince1970 * 1000
    var count = 0

    while (count < t || (NSDate().timeIntervalSince1970 * 1000 - startTime) < data.minTime) {
        x = x.multiply(x).mod(mod)
        count++
        val nowTime = NSDate().timeIntervalSince1970 * 1000
        if (nowTime - startTime > data.maxTime) {
            break
        }
    }

    val time = (NSDate().timeIntervalSince1970 * 1000 - startTime).toLong()
    val signObj = mapOf(
        "runTimes" to count.toLong(),
        "spendTime" to time,
        "t" to count.toLong(),
        "x" to x.toString(16)
    )

    val sortedParams = listOf("runTimes", "spendTime", "t", "x")
    val encodedParams = sortedParams.joinToString("&") { key ->
        val value = signObj[key].toString()
        "${encodeURIComponent(key)}=${encodeURIComponent(value)}"
    }

    // 这里需要确保你工程中有 murmurHash3 的实现
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

// --- 辅助扩展函数 ---

@OptIn(ExperimentalForeignApi::class)
private fun encodeURIComponent(s: String): String {
    val nsString = s as NSString
    // 模拟 JS 的 encodeURIComponent 行为
    val allowedCharacters = NSMutableCharacterSet.alphanumericCharacterSet()
    allowedCharacters.addCharactersInString("-_.!~*'()")
    return nsString.stringByAddingPercentEncodingWithAllowedCharacters(allowedCharacters) ?: s
}
package cc.xiaobaicz.safe.util

import androidx.annotation.StringDef
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * @author BoCheng
 * @date 2019/12/17
 */

const val SHA1 = "SHA1"
const val SHA224 = "SHA224"
const val SHA256 = "SHA256"
const val SHA384 = "SHA384"
const val SHA512 = "SHA512"
const val MD5 = "MD5"

const val HMAC_SHA1 = "HmacSHA1"
const val HMAC_SHA224 = "HmacSHA224"
const val HMAC_SHA256 = "HmacSHA256"
const val HMAC_SHA384 = "HmacSHA384"
const val HMAC_SHA512 = "HmacSHA512"
const val HMAC_MD5 = "HmacMD5"

@Retention(AnnotationRetention.SOURCE)
@StringDef(value = [SHA1, SHA224, SHA256, SHA384, SHA512, MD5])
annotation class SummaryName

@Retention(AnnotationRetention.SOURCE)
@StringDef(value = [HMAC_SHA1, HMAC_SHA224, HMAC_SHA256, HMAC_SHA384, HMAC_SHA512, HMAC_MD5])
annotation class HmacSummaryName

/**
 * 摘要
 */
fun summary(src: ByteArray, @SummaryName summaryName: String): String {
    val digest = MessageDigest.getInstance(summaryName)
    digest.update(src)
    val result = digest.digest()
    val builder = StringBuilder()
    result.forEach {
        builder.append(String.format("%02x", it))
    }
    return builder.toString()
}

/**
 * Hmac摘要
 */
fun hmacSummary(src: ByteArray, @HmacSummaryName hmacSummaryName: String, key: String): String {
    val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), hmacSummaryName)
    val mac = Mac.getInstance(hmacSummaryName)
    mac.init(keySpec)
    mac.update(src)
    val result = mac.doFinal()
    val builder = StringBuilder()
    result.forEach {
        builder.append(String.format("%02x", it))
    }
    return builder.toString()
}
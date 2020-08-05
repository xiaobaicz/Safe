package cc.xiaobaicz.safe.util

import android.util.Base64

/**
 * 字符串DSL
 * @author BoCheng
 * @date 2019/12/04
 */

fun String.summary(@SummaryName summaryName: String): String = summary(toByteArray(Charsets.UTF_8), summaryName)

/**
 * SHA1摘要
 */
fun String.sha1(): String = summary(SHA1)

/**
 * SHA224摘要
 */
fun String.sha224(): String = summary(SHA224)

/**
 * SHA256摘要
 */
fun String.sha256(): String = summary(SHA256)

/**
 * SHA384摘要
 */
fun String.sha384(): String = summary(SHA384)

/**
 * SHA512摘要
 */
fun String.sha512(): String = summary(SHA512)

/**
 * MD5摘要
 */
fun String.md5(): String = summary(MD5)

fun String.hmacSummary(@SummaryName summaryName: String, key: String): String = hmacSummary(toByteArray(Charsets.UTF_8), summaryName, key)

/**
 * HmacSHA1摘要
 */
fun String.hmacSHA1(key: String): String = hmacSummary(HMAC_SHA1, key)

/**
 * HmacSHA224摘要
 */
fun String.hmacSHA224(key: String): String = hmacSummary(HMAC_SHA224, key)

/**
 * HmacSHA256摘要
 */
fun String.hmacSHA256(key: String): String = hmacSummary(HMAC_SHA256, key)

/**
 * HmacSHA384摘要
 */
fun String.hmacSHA384(key: String): String = hmacSummary(HMAC_SHA384, key)

/**
 * HmacSHA512摘要
 */
fun String.hmacSHA512(key: String): String = hmacSummary(HMAC_SHA512, key)

/**
 * HmacMD5摘要
 */
fun String.hmacMD5(key: String): String = hmacSummary(HMAC_MD5, key)

/**
 * base64转码
 */
fun String.base64encode(): String = String(Base64.encode(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT), Charsets.UTF_8)

/**
 * base64转码
 */
fun String.base64decode(): String = String(Base64.decode(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT), Charsets.UTF_8)
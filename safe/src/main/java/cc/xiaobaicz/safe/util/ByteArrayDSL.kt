package cc.xiaobaicz.safe.util

import android.util.Base64

/**
 * @author BoCheng
 * @date 2019/12/17
 */

fun ByteArray.summary(@SummaryName summaryName: String): String = summary(this, summaryName)

/**
 * SHA1摘要
 */
fun ByteArray.sha1(): String = summary(SHA1)

/**
 * SHA224摘要
 */
fun ByteArray.sha224(): String = summary(SHA224)

/**
 * SHA256摘要
 */
fun ByteArray.sha256(): String = summary(SHA256)

/**
 * SHA384摘要
 */
fun ByteArray.sha384(): String = summary(SHA384)

/**
 * SHA512摘要
 */
fun ByteArray.sha512(): String = summary(SHA512)

/**
 * MD5摘要
 */
fun ByteArray.md5(): String = summary(MD5)

fun ByteArray.hmacSummary(@SummaryName summaryName: String, key: String): String = hmacSummary(this, summaryName, key)

/**
 * HmacSHA1摘要
 */
fun ByteArray.hmacSHA1(key: String): String = hmacSummary(HMAC_SHA1, key)

/**
 * HmacSHA224摘要
 */
fun ByteArray.hmacSHA224(key: String): String = hmacSummary(HMAC_SHA224, key)

/**
 * HmacSHA256摘要
 */
fun ByteArray.hmacSHA256(key: String): String = hmacSummary(HMAC_SHA256, key)

/**
 * HmacSHA384摘要
 */
fun ByteArray.hmacSHA384(key: String): String = hmacSummary(HMAC_SHA384, key)

/**
 * HmacSHA512摘要
 */
fun ByteArray.hmacSHA512(key: String): String = hmacSummary(HMAC_SHA512, key)

/**
 * HmacMD5摘要
 */
fun ByteArray.hmacMD5(key: String): String = hmacSummary(HMAC_MD5, key)

/**
 * base64转码
 */
fun ByteArray.base64encode(): String = String(Base64.encode(this, Base64.DEFAULT), Charsets.UTF_8)

/**
 * base64转码
 */
fun ByteArray.base64decode(): ByteArray = Base64.decode(this, Base64.DEFAULT)
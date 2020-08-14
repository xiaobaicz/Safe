package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.global.Constant
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.inv

/**
 * 加密助手
 */
object CipherHelper {

    //AES加密
    fun aesEncode(text: String, accountPassword: String): String {
        val key = aesKey(accountPassword)
        val keyBytes = key2bytes(key)
        val iv = IvParameterSpec(key2iv(keyBytes))
        val cipher = Cipher.getInstance(Constant.AES_ALGORITHM)
        val keySpec = SecretKeySpec(keyBytes, Constant.AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        cipher.update(text.toByteArray())
        return cipher.doFinal().base64encode()
    }

    //AES解密
    fun aesDecode(text: String, accountPassword: String): String {
        val key = aesKey(accountPassword)
        val keyBytes = key2bytes(key)
        val iv = IvParameterSpec(key2iv(keyBytes))
        val cipher = Cipher.getInstance(Constant.AES_ALGORITHM)
        val keySpec = SecretKeySpec(keyBytes, Constant.AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        cipher.update(text.base64decode())
        return String(cipher.doFinal())
    }

    //通过key生成IV
    private fun key2iv(keyBytes: ByteArray): ByteArray {
        val result = keyBytes.clone()
        result.forEachIndexed { index, byte ->
            result[index] = byte.inv()
        }
        return result
    }

    //key字符串转byte数组
    private fun key2bytes(key: String): ByteArray {
        val result = ByteArray(16)
        Regex("[a-z0-9]{2}").findAll(key).forEachIndexed { i, res ->
            result[i] = res.value.toShort(16).toByte()
        }
        return result
    }

    //通过登陆密码生成AES密钥
    private fun aesKey(accountPassword: String) = accountPassword.hmacMD5(Constant.KEY_PASSWORD_HMD5)

}
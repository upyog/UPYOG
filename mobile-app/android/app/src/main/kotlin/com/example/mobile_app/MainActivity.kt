package com.example.mobile_app

import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class MainActivity: FlutterActivity() {
    private val CHANNEL = "flutter.dev/NDPSAESLibrary"

    val pswdIterations = 65536
    val keySize = 256
    val ivBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // Note: this method is invoked on the main thread.
                call, result ->
            val hashMap = call.arguments as HashMap<*,*> //Get the arguments as a HashMap
            val AESMethod = hashMap["AES_Method"]
            val key = hashMap["encKey"]
            val encText = hashMap["text"]

            if (call.method == "NDPSAESInit") {
                try {
                    if(AESMethod == "encrypt") {
                        val encryption = getAtomEncryption(encText.toString(), key.toString())
                        result.success(encryption)
                    }else{
                        val decryption = getAtomDecryption(encText.toString(), key.toString())
                        result.success(decryption)
                    }
                } catch (e: Exception) {
                    result.error("Error", "AES logic failed", null)
                    e.printStackTrace()
                }
            } else {
                result.notImplemented()
            }

        }
    }

    private fun getAtomEncryption(plainText: String, key: String): String {
        val saltBytes = key.toByteArray(charset("UTF-8"))
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val spec = PBEKeySpec(key.toCharArray(), saltBytes, pswdIterations, keySize)
        val secretKey = factory.generateSecret(spec)
        val secret = SecretKeySpec(secretKey.encoded, "AES")
        val localIvParameterSpec = IvParameterSpec(ivBytes)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secret, localIvParameterSpec)
        val encryptedTextBytes = cipher.doFinal(plainText.toByteArray(charset("UTF-8")))
        return byteToHex(encryptedTextBytes)
    }

    private fun byteToHex(byData: ByteArray): String {
        val sb = StringBuffer(byData.size * 2)
        for (i in byData.indices) {
            val v = byData[i].toInt() and 0xFF
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v))
        }
        return sb.toString().toUpperCase()
    }

    private fun getAtomDecryption(encryptedText: String, key: String): String {
        val saltBytes = key.toByteArray(charset("UTF-8"))
        val encryptedTextBytes = hex2ByteArray(encryptedText)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val spec = PBEKeySpec(key.toCharArray(), saltBytes, pswdIterations, keySize)
        val secretKey = factory.generateSecret(spec)
        val secret = SecretKeySpec(secretKey.encoded, "AES")
        val localIvParameterSpec = IvParameterSpec(ivBytes)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(2, secret, localIvParameterSpec)
        var decryptedTextBytes: ByteArray? = null
        decryptedTextBytes = cipher.doFinal(encryptedTextBytes)
        return String(decryptedTextBytes)
    }

    private fun hex2ByteArray(sHexData: String): ByteArray {
        val rawData = ByteArray(sHexData.length / 2)
        for (i in rawData.indices) {
            val index = i * 2
            val v = Integer.parseInt(sHexData.substring(index, index + 2).trim(), 16)
            rawData[i] = v.toByte()
        }
        return rawData
    }
}


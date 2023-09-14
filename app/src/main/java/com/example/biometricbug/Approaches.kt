package com.example.biometricbug

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

class Approach1 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        val prompt = createPrompt(activity) { _, _ ->
            {
                val key = CryptographyManager.createSecretKey("androidx-biometric-auth-method")
                val encryptionCipher = CryptographyManager.getCipher()
                encryptionCipher.init(Cipher.ENCRYPT_MODE, key)
                CryptographyManager.encryptData("hello world", encryptionCipher)
                Toast.makeText(activity, "It worked fine", Toast.LENGTH_LONG).show()
            }
        }
        doPrompt(prompt, null)
    }
}

class Approach2 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        val prompt = createPrompt(activity) { _, _ ->
            {
                val key = CryptographyManager.createSecretKey("androidx-biometric-auth-method-not-required", false)
                val encryptionCipher = CryptographyManager.getCipher()
                encryptionCipher.init(Cipher.ENCRYPT_MODE, key)
                CryptographyManager.encryptData("hello world", encryptionCipher)
                Toast.makeText(activity, "It worked fine", Toast.LENGTH_LONG).show()
            }
        }
        doPrompt(prompt, null)
    }
}

class Approach3 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        val cipher =
            CryptographyManager.getInitializedCipherForEncryption("androidx-biometric-auth-crypto-object-method")
        val prompt = createPrompt(activity) { _, _ ->
            {
                val authedCipher = it!!.cipher!!
                CryptographyManager.encryptData("hello world", authedCipher)
                Toast.makeText(activity, "It worked fine", Toast.LENGTH_LONG).show()
            }
        }
        doPrompt(prompt, BiometricPrompt.CryptoObject(cipher))
    }
}


class Approach4 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        println("Before: $activity")
        val prompt = createPrompt(activity) { calls, prompt ->
            if (calls == 0) {
                {
                    val cipher =
                        CryptographyManager.getInitializedCipherForEncryption("androidx-biometric-auth-wrapped-crypto-object-method")
                    doPrompt(prompt, BiometricPrompt.CryptoObject(cipher))
                }
            } else {
                {
                    CryptographyManager.encryptData("hello world", it!!.cipher!!)
                    Toast.makeText(activity, "It worked fine", Toast.LENGTH_LONG).show()
                }
            }
        }
        doPrompt(prompt, null)
    }
}

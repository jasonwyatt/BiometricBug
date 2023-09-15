package com.example.biometricbug

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


object CryptographyManager {
    private val KEY_SIZE: Int = 256
    val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

    fun getInitializedCipherForEncryption(
        keyName: String,
        userAuthRequired: Boolean = true,
        authEveryTime: Boolean = false
    ): Cipher =
        getInitializedCipherForEncryption(createSecretKey(keyName, userAuthRequired, authEveryTime))

    fun getInitializedCipherForEncryption(key: SecretKey): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher
    }

    fun encryptData(plaintext: String, cipher: Cipher): EncryptedData {
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return EncryptedData(ciphertext,cipher.iv)
    }

    fun getCipher(): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(transformation)
    }

    fun createSecretKey(keyName: String, userAuthRequired: Boolean = true, authEveryTime: Boolean = false): SecretKey {
        // If Secretkey was previously created for that keyName, then delete it first (to keep
        // testing hermetic).
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null) // Keystore must be loaded before it can be accessed
        try {
            keyStore.getKey(keyName, null)?.let {
                Log.i("CryptographyManager", "Deleting existing key with alias: $keyName")
                keyStore.deleteEntry(keyName)
            }
        } catch (e: UnrecoverableKeyException) { /* ignore */ }

        Log.i("CryptographyManager", "Creating key with alias: $keyName")
        val paramsBuilder = KeyGenParameterSpec.Builder(keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
        paramsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            //setIsStrongBoxBacked(true)
            //setInvalidatedByBiometricEnrollment(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (authEveryTime) {
                    setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
                } else {
                    setUserAuthenticationParameters(10, KeyProperties.AUTH_BIOMETRIC_STRONG)
                }
            } else {
                if (authEveryTime) {
                    @Suppress("DEPRECATION")
                    setUserAuthenticationValidityDurationSeconds(-1)
                } else {
                    @Suppress("DEPRECATION")
                    setUserAuthenticationValidityDurationSeconds(10)
                }
            }
            setUserAuthenticationRequired(userAuthRequired)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }
}

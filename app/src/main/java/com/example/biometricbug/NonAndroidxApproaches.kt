@file:Suppress("DEPRECATION")

package com.example.biometricbug

import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

class Approach6 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        val mgr = activity.getSystemService<FingerprintManager>()!!
        val key = CryptographyManager.createSecretKey("approach-one")
        val encryptionCipher = CryptographyManager.getCipher()
        try {
            encryptionCipher.init(Cipher.ENCRYPT_MODE, key)
            mgr.authenticate(
                FingerprintManager.CryptoObject(encryptionCipher)
            ) { result ->
                val resultCipher = result!!.cryptoObject.cipher
                CryptographyManager.encryptData("hello world", resultCipher)
                Log.i("Approach6", "Approach6 worked fine")
                Toast.makeText(activity, "Approach6 worked fine", Toast.LENGTH_LONG).show()
            }
        } catch (e: UserNotAuthenticatedException) {
            mgr.authenticate(null) { authenticateAndEncryptDecrypt(activity) }
        }
    }

    private fun FingerprintManager.authenticate(
        crypto: FingerprintManager.CryptoObject?,
        onSuccess: (AuthenticationResult?) -> Unit
    ) {
        val signal = CancellationSignal()
        authenticate(
            crypto,
            signal,
            0,
            object : FingerprintManager.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                    super.onAuthenticationHelp(helpCode, helpString)
                }
            },
            Handler(Looper.getMainLooper())
        )
    }
}

class Approach7 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        val key = CryptographyManager.createSecretKey("approach-one")
        val encryptionCipher = CryptographyManager.getCipher()
        try {
            encryptionCipher.init(Cipher.ENCRYPT_MODE, key)
            activity.authenticate(
                BiometricPrompt.CryptoObject(encryptionCipher)
            ) { result ->
                val resultCipher = result!!.cryptoObject.cipher
                CryptographyManager.encryptData("hello world", resultCipher)
                Log.i("Approach7", "Approach7 worked fine")
                Toast.makeText(activity, "Approach7 worked fine", Toast.LENGTH_LONG).show()
            }
        } catch (e: UserNotAuthenticatedException) {
            activity.authenticate(null) { authenticateAndEncryptDecrypt(activity) }
        }
    }

    private fun Context.authenticate(crypto: BiometricPrompt.CryptoObject?, onSuccess: (BiometricPrompt.AuthenticationResult?) -> Unit) {
        val prompt = BiometricPrompt.Builder(this)
            .setTitle("Unlock")
            .setDescription("Unlock to proceed")
            .setNegativeButton(
                "Cancel",
                ContextCompat.getMainExecutor(this)
            ) { dialog, _ -> dialog.cancel() }
            .build()
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                onSuccess(result)
            }
        }
        if (crypto == null) {
            prompt.authenticate(CancellationSignal(), ContextCompat.getMainExecutor(this), callback)
        } else {
            prompt.authenticate(crypto, CancellationSignal(), ContextCompat.getMainExecutor(this), callback)
        }
    }
}
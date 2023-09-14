package com.example.biometricbug

import android.os.Handler
import android.os.Looper
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.fragment.app.FragmentActivity

interface BiometricAuthMethod {
    fun authenticateAndEncryptDecrypt(activity: FragmentActivity)

    private class PromptHolder {
        lateinit var prompt: BiometricPrompt
    }

    fun createPrompt(activity: FragmentActivity, callback: ((Int, BiometricPrompt) -> (CryptoObject?) -> Unit)): BiometricPrompt {
        var calls = 0
        val promptHolder = PromptHolder()
        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Post a job to run the callback body after letting UI events process.
                    // This is necessary if the callback may re-launch the prompt.
                    Handler(Looper.getMainLooper())
                        .postDelayed(
                            /* r = */ {
                                callback(calls++, promptHolder.prompt)(result.cryptoObject)
                            },
                            /* token = */ null,
                            /* delayMillis = */ 10
                        )
                }
            }
        )
        promptHolder.prompt = prompt
        return prompt
    }

    fun doPrompt(prompt: BiometricPrompt, obj: CryptoObject?) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setNegativeButtonText("Negative")
            .setDescription("Please Authenticate")
            .build()
        if (obj != null) {
            prompt.authenticate(promptInfo, obj)
        } else {
            prompt.authenticate(promptInfo)
        }
    }
}

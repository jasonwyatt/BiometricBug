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

    /**
     * Creates a [BiometricPrompt] instance.
     *
     * The supplied [callback] will be triggered when the user successfully authenticates with their
     * biometrics. It will be called with a reference to the prompt itself as well as the count of
     * current callback triggers (for approaches that may re-launch the prompt). The [callback]
     * returns a function that is then called with with an optional [CryptoObject].
     */
    fun createPrompt(
        activity: FragmentActivity,
        callback: ((Int, BiometricPrompt) -> (CryptoObject?) -> Unit)
    ): BiometricPrompt {
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

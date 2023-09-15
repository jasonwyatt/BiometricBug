package com.example.biometricbug

import android.app.KeyguardManager
import android.content.Context
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher


/**
 * Approach 1 is the simplest of approaches. We prompt for authentication, then when successful
 * authentication happens: we create the key and encrypt something with it.
 */
class Approach1 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        Log.i("Approach1", "Beginning Approach1")
        val prompt = createPrompt(activity) { _, _ ->
            {
                val key = CryptographyManager.createSecretKey("approach-one")
                val encryptionCipher = CryptographyManager.getCipher()
                encryptionCipher.init(Cipher.ENCRYPT_MODE, key)
                CryptographyManager.encryptData("hello world", encryptionCipher)
                Log.i("Approach1", "Approach1 worked fine")
                Toast.makeText(activity, "Approach1 worked fine", Toast.LENGTH_LONG).show()
            }
        }
        doPrompt(prompt, null)
    }
}

/**
 * Approach 2 is identical to [Approach1], but it specifies that user authentication is _not_
 * required when using the secret key. This means that it will likely always work and should not
 * reproduce the bug.
 */
class Approach2 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        Log.i("Approach2", "Beginning Approach2")
        val prompt = createPrompt(activity) { _, _ ->
            {
                val key = CryptographyManager.createSecretKey("approach-two", false)
                val encryptionCipher = CryptographyManager.getCipher()
                encryptionCipher.init(Cipher.ENCRYPT_MODE, key)
                CryptographyManager.encryptData("hello world", encryptionCipher)
                Log.i("Approach2", "Approach2 worked fine")
                Toast.makeText(activity, "Approach2 worked fine", Toast.LENGTH_LONG).show()
            }
        }
        doPrompt(prompt, null)
    }
}

/**
 * Approach 3 launches the biometric prompt once, but it initializes the encryption cipher before
 * launching the prompt. We have seen crashes with this approach before even launching the prompt
 * because [Cipher.init] throws a `User not authenticated` exception before we even try to use the
 * cipher.
 *
 * On at least one Pixel 6a running Android 13: this approach can be made to crash by repeatedly
 * opening the prompt and cancelling it rapidly.
 */
class Approach3 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        Log.i("Approach3", "Beginning Approach3")
        val prompt = createPrompt(activity) { _, _ ->
            {
                val authedCipher = it!!.cipher!!
                CryptographyManager.encryptData("hello world", authedCipher)
                Log.i("Approach3", "Approach3 worked fine")
                Toast.makeText(activity, "Approach3 worked fine", Toast.LENGTH_LONG).show()
            }
        }
        val cipher = try {
            CryptographyManager.getInitializedCipherForEncryption("approach-three")
        } catch (e: UserNotAuthenticatedException) {
            Toast.makeText(activity, "Failure when initializing Cipher", Toast.LENGTH_LONG)
                .apply { setGravity(Gravity.TOP, 0, 0) }
                .show()
            val backupPrompt = createPrompt(activity) { _, _ ->
                {
                    authenticateAndEncryptDecrypt(activity)
                }
            }
            doPrompt(backupPrompt, null, false)
            return
        }
        doPrompt(prompt, BiometricPrompt.CryptoObject(cipher))
    }
}

/**
 * Approach4 tries to work-around the issue of 'user authentication' not being ready before
 * [Cipher.init] is called when passing the cipher to the biometric prompt by actually launching the
 * prompt _twice_:
 *
 * Once to authenticate the user so we can call [Cipher.init].
 * Once to actually authenticate the [Cipher] (by passing [CryptoObject] to the prompt) before using
 * it.
 */
class Approach4 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        Log.i("Approach4", "Beginning Approach4")
        val prompt = createPrompt(activity) { calls, prompt ->
            if (calls == 0) {
                {
                    Log.i("Approach4", "First authentication complete. creating Cipher.")
                    val cipher =
                        CryptographyManager.getInitializedCipherForEncryption("approach-four")
                    Log.i("Approach4", "First authentication complete. created Cipher.")
                    doPrompt(prompt, BiometricPrompt.CryptoObject(cipher))
                }
            } else {
                {
                    CryptographyManager.encryptData("hello world", it!!.cipher!!)
                    Log.i("Approach4", "Approach4 worked fine")
                    Toast.makeText(activity, "Approach4 worked fine", Toast.LENGTH_LONG).show()
                }
            }
        }
        doPrompt(prompt, null)
    }
}

/**
 * Approach5 is similar to [Approach3] (it passes the CryptoObject), but the key is configured to
 * explicitly require authentication for every usage.
 */
class Approach5 : BiometricAuthMethod {
    override fun authenticateAndEncryptDecrypt(activity: FragmentActivity) {
        Log.i("Approach5", "Beginning Approach5")
        val cipher =
            CryptographyManager.getInitializedCipherForEncryption("approach-five", authEveryTime = true)
        val prompt = createPrompt(activity) { _, _ ->
            {
                CryptographyManager.encryptData("hello world", it!!.cipher!!)
                Log.i("Approach5", "Approach5 worked fine")
                Toast.makeText(activity, "Approach5 worked fine", Toast.LENGTH_LONG).show()
            }
        }
        doPrompt(prompt, BiometricPrompt.CryptoObject(cipher))
    }
}

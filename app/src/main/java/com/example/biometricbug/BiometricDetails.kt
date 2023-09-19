package com.example.biometricbug

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager.FEATURE_HARDWARE_KEYSTORE
import android.content.pm.PackageManager.FEATURE_STRONGBOX_KEYSTORE
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.core.content.getSystemService

data class BiometricDetails internal constructor(
    val androidOsVersion: Int,
    val device: String,
    val isDeviceSecure: Boolean,
    val isKeyguardSecure: Boolean,
    val isDeviceLocked: Boolean,
    val isKeyguardLocked: Boolean,
    val hasStrongbox100: Boolean,
    val hasStrongbox41: Boolean,
    val hasStrongbox40: Boolean,
    val hasHardwareKeystore100: Boolean,
    val hasHardwareKeystore41: Boolean,
    val hasHardwareKeystore40: Boolean,
    val canAuthenticateBiometricStrong: Boolean,
    val canAuthenticateBiometricWeak: Boolean,
    val canAuthenticateDeviceCredential: Boolean,
) {
    companion object {
        fun create(context: Context): BiometricDetails {
            val biometricManager = BiometricManager.from(context)
            val keyguardManager = context.getSystemService<KeyguardManager>()!!
            val pm = context.packageManager

            return BiometricDetails(
                androidOsVersion = Build.VERSION.SDK_INT,
                device = Build.DEVICE,
                isDeviceSecure = keyguardManager.isDeviceSecure,
                isKeyguardSecure = keyguardManager.isKeyguardSecure,
                isDeviceLocked = keyguardManager.isDeviceLocked,
                isKeyguardLocked = keyguardManager.isKeyguardLocked,
                hasStrongbox100 = pm.hasSystemFeature(FEATURE_STRONGBOX_KEYSTORE, 100),
                hasStrongbox41 = pm.hasSystemFeature(FEATURE_STRONGBOX_KEYSTORE, 41),
                hasStrongbox40 = pm.hasSystemFeature(FEATURE_STRONGBOX_KEYSTORE, 40),
                hasHardwareKeystore100 = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    pm.hasSystemFeature(FEATURE_HARDWARE_KEYSTORE, 100)
                } else {
                    false
                },
                hasHardwareKeystore41 = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    pm.hasSystemFeature(FEATURE_HARDWARE_KEYSTORE, 41)
                } else {
                    false
                },
                hasHardwareKeystore40 = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    pm.hasSystemFeature(FEATURE_HARDWARE_KEYSTORE, 40)
                } else {
                    false
                },
                canAuthenticateBiometricStrong = biometricManager
                    .canAuthenticate(BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS,
                canAuthenticateBiometricWeak = biometricManager
                    .canAuthenticate(BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS,
                canAuthenticateDeviceCredential = biometricManager
                    .canAuthenticate(DEVICE_CREDENTIAL) == BIOMETRIC_SUCCESS,
            )
        }
    }
}

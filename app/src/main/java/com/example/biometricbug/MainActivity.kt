package com.example.biometricbug

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.example.biometricbug.ui.theme.BiometricBugTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setShowWhenLocked(true)
        setContent {
            MainActivityContent(this, BiometricDetails.create(this))
        }
    }
}

@Composable
fun MainActivityContent(
    activity: FragmentActivity?,
    biometricDetails: BiometricDetails,
    modifier: Modifier = Modifier
) {
    var details by remember { mutableStateOf(biometricDetails) }
    BiometricBugTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        Approach1()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "Approach 1")
                }
                Button(
                    onClick = {
                        Approach2()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "Approach 2")
                }
                Button(
                    onClick = {
                        Approach3()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "Approach 3")
                }
                Button(
                    onClick = {
                        Approach4()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "Approach 4")
                }
                Button(
                    onClick = {
                        Approach5()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "Approach 5")
                }
                Button(
                    onClick = {
                        Approach6()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "Approach 6")
                }
                Button(
                    onClick = {
                        Approach7()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "Approach 7")
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row {
                        Text(text = "Android OS:")
                        Text(text = "API ${details.androidOsVersion}")
                    }
                    Row {
                        Text(text = "Device:")
                        Text(text = details.device)
                    }
                    Row {
                        Text(text = "isDeviceSecure:")
                        Text(text = "${details.isDeviceSecure}")
                    }
                    Row {
                        Text(text = "isKeyguardSecure:")
                        Text(text = "${details.isKeyguardSecure}")
                    }
                    Row {
                        Text(text = "isDeviceLocked:")
                        Text(text = "${details.isDeviceSecure}")
                    }
                    Row {
                        Text(text = "isKeyguardLocked:")
                        Text(text = "${details.isKeyguardSecure}")
                    }
                    Row {
                        Text(text = "hasStrongbox100:")
                        Text(text = "${details.hasStrongbox100}")
                    }
                    Row {
                        Text(text = "hasStrongbox41:")
                        Text(text = "${details.hasStrongbox41}")
                    }
                    Row {
                        Text(text = "hasStrongbox40:")
                        Text(text = "${details.hasStrongbox40}")
                    }
                    Row {
                        Text(text = "hasHardwareKeystore100:")
                        Text(text = "${details.hasHardwareKeystore100}")
                    }
                    Row {
                        Text(text = "hasHardwareKeystore41:")
                        Text(text = "${details.hasHardwareKeystore41}")
                    }
                    Row {
                        Text(text = "hasHardwareKeystore40:")
                        Text(text = "${details.hasHardwareKeystore40}")
                    }
                    Row {
                        Text(text = "canAuthenticateBiometricStrong:")
                        Text(text = "${details.canAuthenticateBiometricStrong}")
                    }
                    Row {
                        Text(text = "canAuthenticateBiometricWeak:")
                        Text(text = "${details.canAuthenticateBiometricWeak}")
                    }
                    Row {
                        Text(text = "canAuthenticateDeviceCredential:")
                        Text(text = "${details.canAuthenticateDeviceCredential}")
                    }
                }
                Button(onClick = { details = BiometricDetails.create(activity!!) }) {
                    Text("Refresh")
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainActivityPreview() {
    MainActivityContent(
        activity = null,
        biometricDetails = BiometricDetails(
            androidOsVersion = 13,
            device = "Emulator",
            isDeviceSecure = true,
            isKeyguardSecure = true,
            isDeviceLocked = true,
            isKeyguardLocked = true,
            hasStrongbox100 = true,
            hasStrongbox41 = true,
            hasStrongbox40 = true,
            hasHardwareKeystore100 = true,
            hasHardwareKeystore41 = true,
            hasHardwareKeystore40 = true,
            canAuthenticateBiometricStrong = true,
            canAuthenticateBiometricWeak = true,
            canAuthenticateDeviceCredential = true,
        )
    )
}
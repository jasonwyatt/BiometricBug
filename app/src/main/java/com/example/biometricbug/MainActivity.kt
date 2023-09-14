package com.example.biometricbug

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.example.biometricbug.ui.theme.BiometricBugTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainActivityContent(this) }
    }
}

@Composable
fun MainActivityContent(activity: FragmentActivity?, modifier: Modifier = Modifier) {
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
                    Text(text = "androidx.biometric Prompt without CryptoObject")
                }
                Button(
                    onClick = {
                        Approach2()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "androidx.biometric Prompt without CryptoObject\nsetUserAuthenticationRequired(False)")
                }
                Button(
                    onClick = {
                        Approach3()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "androidx.biometric Prompt with CryptoObject")
                }
                Button(
                    onClick = {
                        Approach4()
                            .authenticateAndEncryptDecrypt(activity!!)
                    }
                ) {
                    Text(text = "wrapped androidx.biometric Prompt with CryptoObject")
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainActivityPreview() {
    MainActivityContent(null)
}
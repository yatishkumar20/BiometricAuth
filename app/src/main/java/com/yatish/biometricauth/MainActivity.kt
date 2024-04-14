package com.yatish.biometricauth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yatish.biometricauth.ui.theme.BiometricAuthTheme

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val biometricResult by promptManager.promptResult.collectAsState(initial = null)

                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            print("Activity result: $it")
                        }
                    )

                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                    )
                                }

                                enrollLauncher.launch(enrollIntent)
                            }

                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            promptManager.showBiometricPrompt(
                                "Sample title",
                                "Sample description"
                            )
                        }) {
                            Text(text = "Authenticate")
                        }

                        biometricResult?.let { result ->
                            Text(
                                text = when(result) {
                                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                        result.error
                                    }
                                    is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                                        "Authentication success"
                                    }
                                    is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                                        "Authentication failed"
                                    }
                                    is BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                                        "Authentication not set"
                                    }
                                    is BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                                        "Feature unavailable"
                                    }
                                    is BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                                        "Feature unavailable"
                                    }
                                }
                            )

                        }

                    }
                }

            }
        }
    }
}

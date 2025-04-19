package com.singhDevs.chezz.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.singhDevs.chezz.BuildConfig
import com.singhDevs.chezz.ChezzApplication
import com.singhDevs.chezz.auth.AuthManager
import com.singhDevs.chezz.data.RatingsRepository
import com.singhDevs.chezz.network.AuthService
import com.singhDevs.chezz.network.EmailSignInRequest
import com.singhDevs.chezz.network.GoogleAuthRequest
import com.singhDevs.chezz.network.RetrofitClient
import com.singhDevs.chezz.screens.SignInScreen
import com.singhDevs.chezz.ui.theme.ChezzTheme
import com.singhDevs.chezz.utils.Constants
import com.singhDevs.chezz.viewmodels.AuthViewModel
import com.singhDevs.chezz.viewmodels.SignInViewModel
import com.singhDevs.chezz.viewmodels.SignInViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "SignInActivity"

class SignInActivity : ComponentActivity() {
    private lateinit var authService: AuthService
    private lateinit var authViewModel: AuthViewModel
    private val authManager: AuthManager by lazy {
        (application as ChezzApplication).getAuthManager()
    }

    private lateinit var ratingsRepository: RatingsRepository
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ratingsRepository = RatingsRepository(applicationContext)
        signInViewModel = ViewModelProvider(
            this,
            SignInViewModelFactory(ratingsRepository)
        )[SignInViewModel::class.java]

        authService = RetrofitClient.authServiceInstance
        authViewModel = (application as ChezzApplication).authViewModel


        setContent {
            ChezzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignInScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSignInClick = { email, password ->
                            lifecycleScope.launch {
                                try {
                                    val response = authService.signInWithEmail(EmailSignInRequest(email, password))
                                    if (response.isSuccessful) {
                                        val authResponse = response.body()
                                        if (authResponse == null) {
                                            Log.e(TAG, "Authentication failed: Response body is null")
                                            showError("Authentication failed: Please try again later.")
                                            return@launch
                                        }

                                        // Saving credentials in Encrypted Shared Preferences
                                        authManager.saveUserData(authResponse.token, authResponse.user)
                                        Constants.user = authResponse.user

                                        // Saving ratings in Proto DataStore
                                        Log.d(TAG, "Saving user ratings...")
                                        signInViewModel.saveAllRatings(authResponse.user.ratings)

                                        CoroutineScope(Dispatchers.IO).launch {
                                            authViewModel.checkAuthState()
                                            Log.d(
                                                TAG,
                                                "Auth State updated. Current auth state: ${authViewModel.authState.value}"
                                            )
                                        }
                                        Log.d(
                                            TAG,
                                            "Authentication successful, Received token: ${authResponse.token}"
                                        )
                                        Toast.makeText(
                                            this@SignInActivity,
                                            "Signed in!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    } else {
                                        showError("Authentication failed")
                                        return@launch
                                    }
                                } catch (e: Exception) {
                                    showError("Network error: Error signing you in")
                                    Log.d(TAG, "Network error: ${e.message}")
                                    return@launch
                                }
                            }
                        },
                        onGoogleSignInClick = { initiateGoogleSignIn() },
                        onSignUpClick = {
                            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                            startActivity(intent)
                        },
                    )
                }
            }
        }
    }

    private fun initiateGoogleSignIn() {
        val googleWebClientId = BuildConfig.googleWebClientId

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Allow all accounts
            .setServerClientId(googleWebClientId)
            .setAutoSelectEnabled(false) // Show account picker
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(applicationContext)

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@SignInActivity
                )
                handleGoogleSignInResult(result)
            } catch (e: Exception) {
                handleSignInError(e)
            }
        }
    }

    private fun handleSignInError(e: Exception) {
        Log.d(TAG, "handleSignInError: " + e.message)
        Toast.makeText(this@SignInActivity, "Error signing you in. Please try again later.", Toast.LENGTH_LONG).show()
    }

    private fun handleGoogleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                lifecycleScope.launch {
                    try {
                        val response =
                            authService.authenticateWithGoogle(GoogleAuthRequest(idToken))

                        if (response.isSuccessful) {
                            val authResponse = response.body()
                            if (authResponse == null) {
                                Log.e(TAG, "Authentication failed: Response body is null")
                                showError("Authentication failed: Please try again later.")
                                return@launch
                            }

                            // Saving credentials in Encrypted Shared Preferences
                            authManager.saveUserData(authResponse.token, authResponse.user)
                            Constants.user = authResponse.user

                            // Saving ratings in Proto DataStore
                            Log.d(TAG, "Saving user ratings...")
                            signInViewModel.saveAllRatings(authResponse.user.ratings)

                            CoroutineScope(Dispatchers.IO).launch {
                                authViewModel.checkAuthState()
                                Log.d(
                                    TAG,
                                    "Auth State updated. Current auth state: ${authViewModel.authState.value}"
                                )
                            }
                            Log.d(
                                TAG,
                                "Authentication successful, Received token: ${authResponse.token}"
                            )
                            Toast.makeText(
                                this@SignInActivity,
                                "Signed in!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            showError("Authentication failed")
                            return@launch
                        }
                    } catch (e: Exception) {
                        showError("Network error: Error signing you in")
                        Log.d(TAG, "Network error: ${e.message}")
                        return@launch
                    }
                }

            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "Invalid Google token")
                showError("Invalid Google token")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
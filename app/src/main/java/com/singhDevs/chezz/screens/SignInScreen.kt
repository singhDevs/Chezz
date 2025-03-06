package com.singhDevs.chezz.screens

import android.content.Context
import android.credentials.GetCredentialResponse
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.security.crypto.EncryptedSharedPreferences
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.singhDevs.chezz.R
import com.singhDevs.chezz.components.EmailTextField
import com.singhDevs.chezz.components.PasswordTextField
import com.singhDevs.chezz.network.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onGoogleSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmailTextField(
                    modifier = Modifier.padding(10.dp),
                    email = email,
                    onEmailChange = { email = it }
                )
                PasswordTextField(
                    modifier = Modifier.padding(10.dp),
                    password = password,
                    onPasswordChange = { password = it },
                    leadingIcon = painterResource(id = R.drawable.ic_lock)
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp),
                    onClick = {
                        Toast.makeText(context, "Sign in clicked", Toast.LENGTH_SHORT).show()
                    }) {
                    Text("Sign in")
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp),
                    onClick = {
                        onGoogleSignInClick()
                    }) {
                    Text("Sign in with Google")
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    SignInScreen(){}
}
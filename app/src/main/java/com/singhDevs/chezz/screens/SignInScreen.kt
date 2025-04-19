package com.singhDevs.chezz.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.components.ForgotPasswordDialog

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onSignUpClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val darkBackground = Color(0xFF1A1A1A)
    val primaryAmber = Color(0xFFBF8F3F)       // Gold/amber color from knight's accents
    val textColor = Color(0xFFE0E0E0)          // Light gray for text
    val accentBrown = Color(0xFF8B5A2B)        // Darker accent for buttons


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .background(darkBackground)
    ) {
        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                context = LocalContext.current,
                onDismiss = { showForgotPasswordDialog = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App logo
            Image(
                painter = painterResource(id = R.drawable.ic_chezz_horse),
                contentDescription = "Chess App Logo",
                modifier = Modifier
                    .size(160.dp)
                    .padding(top = 28.dp, bottom = 28.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Welcome text
            Text(
                text = "Welcome to Chezz",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = primaryAmber
            )

            Text(
                text = "Sign in to continue",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraLight,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 3.dp, bottom = 32.dp)
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = primaryAmber
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryAmber,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = primaryAmber,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = primaryAmber,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = primaryAmber
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = if (passwordVisible) painterResource(R.drawable.ic_eye_off) else painterResource(
                                R.drawable.ic_eye
                            ),
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                            tint = primaryAmber
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryAmber,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = primaryAmber,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = primaryAmber,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Box(modifier = Modifier.fillMaxWidth()){
                Text(
                    text = "Forgot Password?",
                    color = primaryAmber,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .align(Alignment.TopEnd)
                        .clickable { showForgotPasswordDialog = true }
                )
            }

            Button(
                onClick = { onSignInClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentBrown
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Or divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.5f)
                )
                Text(
                    text = " or  ",
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Google Sign-in button
            OutlinedButton(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = textColor
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(Color.Gray.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        tint = Color.Unspecified, // Use original Google colors
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign up prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                TextButton(onClick = onSignUpClick) {
                    Text(
                        text = "Sign Up",
                        color = primaryAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Chess pattern overlay at bottom (subtle)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    ChessboardPattern(
                        lightSquareColor = Color.White.copy(alpha = 0.03f),
                        darkSquareColor = Color.Transparent
                    )
                )
        )
    }
}

// Chess pattern brush for decorative elements
@Composable
fun ChessboardPattern(
    lightSquareColor: Color,
    darkSquareColor: Color
): Brush {
    return Brush.verticalGradient(
        colors = listOf(Color.Transparent, darkSquareColor),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )
}
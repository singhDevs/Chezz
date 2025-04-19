package com.singhDevs.chezz.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.singhDevs.chezz.R
import com.singhDevs.chezz.components.LinearProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.singhDevs.chezz.activities.SignUpActivity


@Composable
fun SignUpScreen(
    modifier: Modifier,
    context: Context,
    onNavigateToLogin: () -> Unit,
    onSignUpClick: (String, String) -> Unit
) {
    var progress by remember { mutableFloatStateOf(1f) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    // Animation states
    val coroutineScope = rememberCoroutineScope()
    var showContent by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordMismatchError by remember { mutableStateOf(false) }

    // Define colors from the Chezz logo
    val deepDarkBg = Color(0xFF121212)
    val surfaceDark = Color(0xFF1E1E1E)
    val chezzBrown = Color(0xFF5D3A1F)
    val chezzLightBrown = Color(0xFF8B5A2B)
    val chezzGray = Color(0xFF8D8D8D)
    val accentGray = Color(0xFFB0B0B0)
    val textColor = Color(0xFFE0E0E0)
    val fieldBackground = Color(0xFF2A2A2A)

    // Initial animation
    LaunchedEffect(Unit) {
        delay(500)
        showContent = true
    }

    // Function to validate email format
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(durationMillis = 800))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Step indicator
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        indicatorProgress = progress
                    )

                    // Step 1: Email
                    AnimatedVisibility(
                        visible = progress == 1f,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Custom TextField - Email
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = it.isNotEmpty() && !isValidEmail(it)
                                },
                                label = {
                                    Text(
                                        text = "Email Address",
                                        modifier = Modifier.background(Color.Transparent)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Email,
                                        contentDescription = "Email",
                                        tint = if (emailError) MaterialTheme.colorScheme.error else chezzLightBrown
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = LocalTextStyle.current.copy(color = textColor),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = chezzLightBrown,
                                    unfocusedBorderColor = chezzGray.copy(alpha = 0.5f),
                                    focusedLabelColor = chezzLightBrown,
                                    cursorColor = chezzLightBrown,
                                    focusedContainerColor = surfaceDark.copy(alpha = 0.6f),
                                    unfocusedContainerColor = surfaceDark
                                ),
                                isError = emailError,
                                supportingText = {
                                    if (emailError) {
                                        Text("Please enter a valid email address")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Next button
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = {
                                        if (isValidEmail(email)) {
                                            coroutineScope.launch {
                                                progress = 2f
                                            }
                                        } else {
                                            emailError = !isValidEmail(email)
                                        }
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(15.dp),
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = colorResource(R.color.primary_amber)
                                    )
                                }
                            }
                        }
                    }

                    // Step 2: Password creation
                    AnimatedVisibility(
                        visible = progress == 2f,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Password strength indicator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            ) {
                                val passwordStrength = when {
                                    password.isEmpty() -> 0
                                    password.length < 6 -> 1
                                    password.length < 8 -> 2
                                    password.contains(Regex("[A-Z]")) &&
                                            password.contains(Regex("[a-z]")) &&
                                            password.contains(Regex("[0-9]")) -> 3

                                    else -> 2
                                }

                                val strengthColor = when (passwordStrength) {
                                    0 -> Color.White.copy(alpha = 0.6f)
                                    1 -> Color.Red.copy(alpha = 0.8f)
                                    2 -> Color(0xFFFFA500) // Orange
                                    else -> Color(0xFF4CAF50) // Green
                                }

                                val strengthText = when (passwordStrength) {
                                    0 -> "Password Strength"
                                    1 -> "Weak"
                                    2 -> "Medium"
                                    else -> "Strong"
                                }

                                Text(
                                    text = strengthText,
                                    fontSize = 14.sp,
                                    color = strengthColor,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(8.dp)
                                )

                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = { passwordStrength / 3f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .align(Alignment.BottomCenter),
                                    color = strengthColor,
                                    trackColor = Color.Gray.copy(alpha = 0.2f)
                                )
                            }

                            // Password Field
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    passwordError = it.length < 6
                                    passwordMismatchError =
                                        confirmPassword.isNotEmpty() && it != confirmPassword
                                },
                                label = {
                                    Text(
                                        text = "Create Password",
                                        modifier = Modifier.background(Color.Transparent)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = "Password",
                                        tint = if (passwordError) MaterialTheme.colorScheme.error else chezzLightBrown
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            painter = if (passwordVisible) painterResource(R.drawable.ic_eye_off) else painterResource(
                                                R.drawable.ic_eye
                                            ),
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = chezzGray
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = LocalTextStyle.current.copy(color = textColor),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = chezzLightBrown,
                                    unfocusedBorderColor = chezzGray.copy(alpha = 0.5f),
                                    focusedLabelColor = chezzLightBrown,
                                    cursorColor = chezzLightBrown,
                                    focusedContainerColor = surfaceDark.copy(alpha = 0.6f),
                                    unfocusedContainerColor = surfaceDark
                                ),
                                isError = passwordError,
                                supportingText = {
                                    if (passwordError) {
                                        Text("Password must be at least 6 characters")
                                    }
                                }
                            )

                            // Confirm Password Field
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    passwordMismatchError = it != password
                                },
                                label = {
                                    Text(
                                        text = "Confirm Password",
                                        modifier = Modifier.background(Color.Transparent)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = "Confirm Password",
                                        tint = if (passwordMismatchError) MaterialTheme.colorScheme.error else chezzLightBrown
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        confirmPasswordVisible = !confirmPasswordVisible
                                    }) {
                                        Icon(
                                            painter = if (confirmPasswordVisible) painterResource(R.drawable.ic_eye_off) else painterResource(
                                                R.drawable.ic_eye
                                            ),
                                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                            tint = chezzGray
                                        )
                                    }
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 25.dp),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = LocalTextStyle.current.copy(color = textColor),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = chezzLightBrown,
                                    unfocusedBorderColor = chezzGray.copy(alpha = 0.5f),
                                    focusedLabelColor = chezzLightBrown,
                                    cursorColor = chezzLightBrown,
                                    focusedContainerColor = surfaceDark.copy(alpha = 0.6f),
                                    unfocusedContainerColor = surfaceDark
                                ),
                                isError = passwordMismatchError,
                                supportingText = {
                                    if (passwordMismatchError) {
                                        Text("Passwords don't match")
                                    }
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            ) {
                                // Back button
                                IconButton(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .align(Alignment.TopStart)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = { progress = 1f }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(15.dp),
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = null,
                                        tint = colorResource(R.color.primary_amber)
                                    )
                                }

                                // Next button
                                IconButton(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = {
                                        if (password.length >= 6 && password == confirmPassword) {
                                            progress = 3f
                                        } else {
                                            passwordError = password.length < 6
                                            passwordMismatchError = password != confirmPassword
                                        }
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(15.dp),
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = colorResource(R.color.primary_amber)
                                    )
                                }
                            }
                        }
                    }

                    // Step 3: Final step
                    AnimatedVisibility(
                        visible = progress == 3f,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_terms),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(top = 25.dp, bottom = 15.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 10.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(surfaceDark.copy(alpha = 0.4f))
                            ) {
                                Checkbox(
                                    checked = termsAccepted,
                                    onCheckedChange = { termsAccepted = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = chezzBrown,
                                        uncheckedColor = chezzGray.copy(alpha = 0.7f),
                                        checkmarkColor = Color.White
                                    )
                                )

                                Text(
                                    text = "I agree to the ",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                                Text(
                                    text = "T&C and Privacy Policy",
                                    color = colorResource(R.color.teal_200),
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            "https://singhdevs.github.io/chezz/T&C/".toUri()
                                        )
                                        context.startActivity(intent)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            ) {
                                // Back button
                                IconButton(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .align(Alignment.TopStart)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = { progress = 2f }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(15.dp),
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = null,
                                        tint = colorResource(R.color.primary_amber)
                                    )
                                }

                                // Next button
                                IconButton(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(fieldBackground),
                                    onClick = {
                                        if (termsAccepted) {
                                            onSignUpClick(email, password)
                                        }
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(15.dp),
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = colorResource(R.color.primary_amber)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Already have an account text at the bottom
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000, delayMillis = 300)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    color = accentGray
                )
                Text(
                    text = "Sign In",
                    color = chezzLightBrown,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SignUpPreview() {
    SignUpScreen(Modifier, SignUpActivity(), {}, { _, _ -> })
}
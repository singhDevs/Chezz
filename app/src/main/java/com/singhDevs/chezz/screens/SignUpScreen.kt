package com.singhDevs.chezz.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier,
    onNavigateToLogin: () -> Unit,
    onSignUpClick: (String, String, String) -> Unit
) {
    // State variables
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    // Animation states
    val coroutineScope = rememberCoroutineScope()
    var showContent by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(true) }
    var currentStep by remember { mutableStateOf(0) }

    // Validation states
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordMismatchError by remember { mutableStateOf(false) }

    // Define colors from the Chezz logo
    val deepDarkBg = Color(0xFF121212)
    val surfaceDark = Color(0xFF1E1E1E)
    val chezzBrown = Color(0xFF5D3A1F)
    val chezzLightBrown = Color(0xFF8B5A2B)
    val chezzDarkBrown = Color(0xFF3D2614)
    val chezzGray = Color(0xFF8D8D8D)
    val accentGray = Color(0xFFB0B0B0)

    // Define gradients
    val primaryGradient = Brush.linearGradient(
        colors = listOf(chezzBrown, chezzLightBrown),
        start = Offset(0f, 0f),
        end = Offset(100f, 100f)
    )

    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            deepDarkBg
        ),
        radius = 1500f
    )

    // Initial animation
    LaunchedEffect(Unit) {
        delay(500)
        showLogo = false
        delay(600)
        showContent = true
    }

    // Function to validate email format
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        // Chess pattern background (subtle)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.03f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 500f,
                        endY = 2000f
                    )
                )
        )

        // Top decorative element
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-140).dp)
                .size(300.dp)
                .alpha(0.4f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            chezzLightBrown.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Animated logo entry
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chezz_horse),
                    contentDescription = "Chezz Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(8.dp)
                )
            }

            // Main content
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(durationMillis = 800))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Small logo at the top
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 5.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_chezz_horse),
                            contentDescription = "Chezz Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "CHEZZ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(chezzLightBrown, accentGray)
                                )
                            )
                        )
                    }

                    // Title with step indicator
                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Start your chess journey",
                        fontSize = 16.sp,
                        color = accentGray,
                        modifier = Modifier.padding(bottom = 36.dp)
                    )

                    // Step indicators
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    ) {
                        for (i in 0..2) {
                            val isActive = i <= currentStep
                            val color = if (isActive) chezzLightBrown else chezzGray.copy(alpha = 0.3f)

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(
                                        width = if (i == currentStep) 32.dp else 24.dp,
                                        height = 3.dp
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                            )
                        }
                    }

                    // Step 1: Username and Email
                    AnimatedVisibility(
                        visible = currentStep == 0,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Custom TextField - Username
                            OutlinedTextField(
                                value = username,
                                onValueChange = {
                                    username = it
                                    usernameError = it.length < 3
                                },
                                label = { Text("Create Username") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = "Username",
                                        tint = if (usernameError) MaterialTheme.colorScheme.error else chezzLightBrown
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = chezzLightBrown,
                                    unfocusedBorderColor = chezzGray.copy(alpha = 0.5f),
                                    focusedLabelColor = chezzLightBrown,
                                    cursorColor = chezzLightBrown,
                                    focusedContainerColor = surfaceDark.copy(alpha = 0.6f),
                                    unfocusedContainerColor = surfaceDark
                                ),
                                isError = usernameError,
                                supportingText = {
                                    if (usernameError) {
                                        Text("Username must be at least 3 characters")
                                    }
                                }
                            )

                            /*OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = chezzLightBrown,
                                unfocusedBorderColor = chezzGray.copy(alpha = 0.5f),
                                focusedLabelColor = chezzLightBrown,
                                cursorColor = chezzLightBrown,
                                containerColor = surfaceDark.copy(alpha = 0.6f)
                            )*/
                            Spacer(modifier = Modifier.height(16.dp))

                            // Custom TextField - Email
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = it.isNotEmpty() && !isValidEmail(it)
                                },
                                label = { Text("Email Address") },
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
                            Button(
                                onClick = {
                                    if (username.length >= 3 && isValidEmail(email)) {
                                        coroutineScope.launch {
                                            currentStep = 1
                                        }
                                    } else {
                                        usernameError = username.length < 3
                                        emailError = !isValidEmail(email)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = primaryGradient,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "CONTINUE",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }

                    // Step 2: Password creation
                    AnimatedVisibility(
                        visible = currentStep == 1,
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

                                val strengthColor = when(passwordStrength) {
                                    0 -> Color.Gray.copy(alpha = 0.3f)
                                    1 -> Color.Red.copy(alpha = 0.8f)
                                    2 -> Color(0xFFFFA500) // Orange
                                    else -> Color(0xFF4CAF50) // Green
                                }

                                val strengthText = when(passwordStrength) {
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

                                LinearProgressIndicator(
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
                                    passwordMismatchError = confirmPassword.isNotEmpty() && it != confirmPassword
                                },
                                label = { Text("Create Password") },
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
                                            imageVector = if (passwordVisible) Icons.Rounded.Clear else Icons.Rounded.Check,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = chezzGray
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
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
                                label = { Text("Confirm Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = "Confirm Password",
                                        tint = if (passwordMismatchError) MaterialTheme.colorScheme.error else chezzLightBrown
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Rounded.Clear else Icons.Rounded.Check,
                                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                            tint = chezzGray
                                        )
                                    }
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                shape = RoundedCornerShape(12.dp),
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

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            ) {
                                // Back button
                                OutlinedButton(
                                    onClick = { currentStep = 0 },
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, chezzGray.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = "BACK",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Next button
                                Button(
                                    onClick = {
                                        if (password.length >= 6 && password == confirmPassword) {
                                            currentStep = 2
                                        } else {
                                            passwordError = password.length < 6
                                            passwordMismatchError = password != confirmPassword
                                        }
                                    },
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = primaryGradient,
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "CONTINUE",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Step 3: Final step
                    AnimatedVisibility(
                        visible = currentStep == 2,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Chess piece illustration
                            Image(
                                painter = painterResource(id = R.drawable.bn),
                                contentDescription = "Chess Knight",
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(bottom = 24.dp)
                            )

                            // Terms and conditions checkbox with custom design
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(surfaceDark.copy(alpha = 0.4f))
                                    .padding(16.dp)
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
                                    text = "I agree to the Terms of Service and Privacy Policy",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Complete registration button
                            Button(
                                onClick = {
                                    if (termsAccepted) {
                                        onSignUpClick(username, email, password)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                                ),
                                contentPadding = PaddingValues(0.dp),
                                enabled = termsAccepted
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = if (termsAccepted) primaryGradient else Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Gray.copy(alpha = 0.5f),
                                                    Color.Gray.copy(alpha = 0.3f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "COMPLETE REGISTRATION",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Rounded.ArrowForward,
                                            contentDescription = "Complete",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Back button
                            OutlinedButton(
                                onClick = { currentStep = 1 },
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, chezzGray.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = "Back",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "BACK",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Social sign-up options with custom styling
                            Text(
                                text = "OR SIGN UP WITH",
                                fontSize = 12.sp,
                                color = chezzGray,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Google
                                SocialSignInButton(
                                    icon = painterResource(R.drawable.ic_google),
                                    backgroundColor = Color(0xFF4285F4),
                                    onClick = { /* Google sign-up logic */ }
                                )

                                /*// Facebook
                                SocialSignInButton(
                                    icon = Icons.Rounded.,
                                    backgroundColor = Color(0xFF3B5998),
                                    onClick = { *//* Facebook sign-up logic *//* }
                                )

                                // Twitter/X
                                SocialSignInButton(
                                    icon = Icons.Rounded.Send,
                                    backgroundColor = Color(0xFF1DA1F2),
                                    onClick = { *//* Twitter/X sign-up logic *//* }
                                )

                                // Apple
                                SocialSignInButton(
                                    icon = Icons.Rounded.,
                                    backgroundColor = Color.White,
                                    iconTint = Color.Black,
                                    onClick = { *//* Apple sign-up logic *//* }
                                )*/
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

@Composable
fun SocialSignInButton(
    icon: Painter,
    backgroundColor: Color,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = "Social Sign In",
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview
@Composable
private fun SignUpPreview() {
    SignUpScreen(Modifier, {}, {_, _, _ ->})
}
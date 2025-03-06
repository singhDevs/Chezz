package com.singhDevs.chezz.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.singhDevs.chezz.R

@Composable
fun PasswordTextField(
    modifier: Modifier,
    password: String,
    onPasswordChange: (String) -> Unit,
    leadingIcon: Painter
) {
    CustomTextField(
        modifier = modifier,
        value = password,
        onValueChange = onPasswordChange,
        leadingIcon = leadingIcon,
        isPassword = true
    )
}
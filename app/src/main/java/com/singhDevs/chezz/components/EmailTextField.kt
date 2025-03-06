package com.singhDevs.chezz.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.singhDevs.chezz.R

@Composable
fun EmailTextField(
    modifier: Modifier,
    email: String,
    onEmailChange: (String) -> Unit
) {
    CustomTextField(
        modifier = modifier,
        value = email,
        onValueChange = onEmailChange,
        leadingIcon = painterResource(R.drawable.ic_email)
    )
}
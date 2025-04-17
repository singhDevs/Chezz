package com.singhDevs.chezz.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NotificationPermissionRationaleDialog(
    onAllow: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enable Notifications") },
        text = {
            Text(
                text = "This app uses notifications to keep you updated with the latest information. Would you like to enable notifications?"
            )
        },
        confirmButton = {
            TextButton(onClick = onAllow) {
                Text(text = "Allow")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "No Thanks")
            }
        }
    )
}
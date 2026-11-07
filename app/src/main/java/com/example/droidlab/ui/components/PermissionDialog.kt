package com.example.droidlab.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AudioPermissionDialog(
    isPermanentlyDenied: Boolean,
    onGrantClick: () -> Unit,
    onDismissRequest: () -> Unit,
    context: Context
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(name = "music", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (isPermanentlyDenied) "Permission Required" else "Access Local Music",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isPermanentlyDenied) {
                    Text(
                        text = "Audio permission is required to index and play local music on your device. Please enable audio or storage permission in app settings.",
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                } else {
                    Text(
                        text = "Droid Lab needs permission to scan and play audio files stored on your device. Your local tracks remain completely private and offline.",
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isPermanentlyDenied) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                        onDismissRequest()
                    } else {
                        onGrantClick()
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isPermanentlyDenied) "Open Settings" else "Grant Access")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Not Now")
            }
        }
    )
}

@Composable
fun NotificationPermissionDialog(
    onGrantClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(name = "tune", size = 22.dp, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Playback Notification",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enable notifications so you can control playback, see album artwork, and skip tracks from your lock screen and notification bar.",
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGrantClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Allow")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Not Now")
            }
        }
    )
}

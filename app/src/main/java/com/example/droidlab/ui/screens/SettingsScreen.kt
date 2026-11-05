package com.example.droidlab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.droidlab.ui.components.AppIcon

/**
 * Settings Screen accessible from Droid Lab Navigation Drawer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        AppIcon(name = "arrow_back", size = 22.dp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ListItem(
                headlineContent = { Text("App Preferences", fontWeight = FontWeight.Medium) },
                supportingContent = { Text("Manage notifications and security") },
                leadingContent = { AppIcon(name = "settings", size = 22.dp) }
            )
        }
    }
}

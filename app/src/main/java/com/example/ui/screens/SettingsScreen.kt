package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
    val provider by settingsViewModel.aiProvider.collectAsStateWithLifecycle()
    val apiKey by settingsViewModel.customApiKey.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pengaturan & Profil") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Theme Setting
            Column {
                Text("Tema Aplikasi", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Sistem", "Terang", "Gelap").forEach { t ->
                        FilterChip(
                            selected = theme == t,
                            onClick = { settingsViewModel.setTheme(t) },
                            label = { Text(t) }
                        )
                    }
                }
            }

            // AI Provider Setting
            Column {
                Text("Penyedia Ai Assist", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Gemini", "OpenRouter", "Groq").forEach { p ->
                        FilterChip(
                            selected = provider == p,
                            onClick = { settingsViewModel.setAiProvider(p) },
                            label = { Text(p) }
                        )
                    }
                }
            }

            // Custom API Key Setting
            Column {
                Text("Kunci API (Opsional / Kustom)", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { settingsViewModel.setCustomApiKey(it) },
                    placeholder = { Text("Masukkan API Key Anda") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Text("Gunakan kunci dari $provider untuk customisasi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

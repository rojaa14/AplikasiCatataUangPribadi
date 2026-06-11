package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AiViewModel
import com.example.ui.ExpenseViewModel
import com.example.ui.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    expenseViewModel: ExpenseViewModel,
    aiViewModel: AiViewModel,
    settingsViewModel: SettingsViewModel
) {
    val expenses by expenseViewModel.expenses.collectAsStateWithLifecycle()
    val advice by aiViewModel.advice.collectAsStateWithLifecycle()
    val isLoading by aiViewModel.isLoading.collectAsStateWithLifecycle()
    
    val provider by settingsViewModel.aiProvider.collectAsStateWithLifecycle()
    val customKey by settingsViewModel.customApiKey.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ai Assist (${provider})") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    aiViewModel.getAdvice(provider, customKey, expenses)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if(isLoading) "Menganalisis..." else "Minta Saran Hemat", fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (advice.isNotBlank()) {
                OutlinedCard(modifier = Modifier.fillMaxWidth().weight(1f), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                        Text(advice, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

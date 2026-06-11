package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController, viewModel: ExpenseViewModel) {
    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Pengeluaran") } // "Pemasukan" atau "Pengeluaran"
    var category by remember { mutableStateOf("Umum") }

    val categories = if (type == "Pengeluaran") {
        listOf("Makan", "Transport", "Hiburan", "Tagihan", "Kesehatan", "Umum")
    } else {
        listOf("Gaji", "Bonus", "Hadiah", "Investasi", "Lainnya")
    }

    // Reset category when type changes if not in new list
    LaunchedEffect(type) {
        if (!categories.contains(category)) {
            category = categories.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = type == "Pengeluaran",
                    onClick = { type = "Pengeluaran" },
                    label = { Text("Pengeluaran") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = type == "Pemasukan",
                    onClick = { type = "Pemasukan" },
                    label = { Text("Pemasukan") }
                )
            }

            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Nominal (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Kategori", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            
            // Chips layout using Row/Column for simplicity
            Column {
                val chunked = categories.chunked(3)
                chunked.forEach { rowCategories ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowCategories.forEach { cat ->
                            ElevatedFilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        viewModel.addExpense(
                            amount = amount,
                            type = type,
                            category = category,
                            date = System.currentTimeMillis(),
                            note = note
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = amountStr.isNotBlank()
            ) {
                Text("Simpan Transaksi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

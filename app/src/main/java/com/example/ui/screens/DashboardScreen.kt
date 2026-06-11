package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.ExpenseEntity
import com.example.ui.ExpenseViewModel
import com.example.ui.theme.ExpenseColor
import com.example.ui.theme.IncomeColor
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: ExpenseViewModel) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    val totalIncome = expenses.filter { it.type == "Pemasukan" }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.type == "Pengeluaran" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keuangan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                BalanceCard(balance, totalIncome, totalExpense)
            }
            
            item {
                if (expenses.isNotEmpty()) {
                    Text("Grafik Bulanan", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                    MonthlyChartCard(expenses)
                }
            }

            item {
                Text("Transaksi Terakhir", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))
            }

            if (expenses.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada transaksi.", color = Color.Gray)
                    }
                }
            } else {
                items(expenses) { expense ->
                    TransactionItem(expense = expense, onDelete = { viewModel.deleteExpense(it) })
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, income: Double, expense: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Total Saldo", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 14.sp)
            Text(formatRupiah(balance), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(8.dp)).background(IncomeColor.copy(alpha=0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = IncomeColor, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pemasukan", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Text(formatRupiah(income), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top=4.dp))
                }
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(8.dp)).background(ExpenseColor.copy(alpha=0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = ExpenseColor, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pengeluaran", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Text(formatRupiah(expense), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top=4.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(expense: ExpenseEntity, onDelete: (ExpenseEntity) -> Unit) {
    val isIncome = expense.type == "Pemasukan"
    val color = if (isIncome) IncomeColor else ExpenseColor
    val icon = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
    
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val dateString = sdf.format(Date(expense.date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (expense.note.isNotEmpty()) {
                    Text(expense.note, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                }
                Text(dateString, fontSize = 12.sp, color = Color.Gray)
            }
            
            Text(
                text = "${if(isIncome) "+" else "-"}${formatRupiah(expense.amount)}",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun MonthlyChartCard(expenses: List<ExpenseEntity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
            val incomeColor = IncomeColor
            val expenseColor = ExpenseColor
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = System.currentTimeMillis()
                val currentMonth = cal.get(Calendar.MONTH)
                
                val monthlyData = expenses.filter { 
                    val exCal = Calendar.getInstance()
                    exCal.timeInMillis = it.date
                    exCal.get(Calendar.MONTH) == currentMonth
                }
                
                val income = monthlyData.filter { it.type == "Pemasukan" }.sumOf { it.amount }.toFloat()
                val expense = monthlyData.filter { it.type == "Pengeluaran" }.sumOf { it.amount }.toFloat()
                
                val total = income + expense
                if (total == 0f) return@Canvas
                
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barWidth = 60.dp.toPx()
                val spacing = 40.dp.toPx()
                
                val maxVal = maxOf(income, expense)
                val scale = if(maxVal > 0) canvasHeight * 0.8f / maxVal else 0f
                
                val startX = (canvasWidth - (barWidth * 2 + spacing)) / 2
                
                // Income Bar
                drawRoundRect(
                    color = incomeColor,
                    topLeft = Offset(startX, canvasHeight - (income * scale)),
                    size = Size(barWidth, income * scale),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                
                // Expense Bar
                drawRoundRect(
                    color = expenseColor,
                    topLeft = Offset(startX + barWidth + spacing, canvasHeight - (expense * scale)),
                    size = Size(barWidth, expense * scale),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
            }
            
            // Labels manually overlaid for simplicity
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Pemasukan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(40.dp))
                Text("Pengeluaran", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(amount).replace("Rp", "Rp ").substringBefore(",")
}

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
import com.example.ui.theme.GraphBarActive
import com.example.ui.theme.GraphBarDefault
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Keuangan Kita", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp, color = MaterialTheme.colorScheme.onBackground)
                    Text("Halo, Aris. Selamat pagi!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                com.example.ui.components.AnimatedIconButton(icon = Icons.Default.Add, contentDescription = "Tambah", onClick = { navController.navigate("add") })
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            item {
                BalanceCard(balance, totalIncome, totalExpense)
            }
            
            item {
                if (expenses.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Grafik Bulanan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Lihat Detail", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = GraphBarActive)
                    }
                    MonthlyChartCard(expenses)
                }
            }

            item {
                Text("Transaksi Terakhir", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp).padding(horizontal = 4.dp))
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
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Total Saldo Anda", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Text(formatRupiah(balance), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
            
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(bottom = 16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(50)).background(Color.Green.copy(alpha=0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("PEMASUKAN", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                        Text("+ ${formatRupiah(income)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top=2.dp))
                    }
                }
                
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(50)).background(Color.Red.copy(alpha=0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("PENGELUARAN", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                        Text("- ${formatRupiah(expense)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top=2.dp))
                    }
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
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.category, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                val subtitle = if (expense.note.isNotEmpty()) "${expense.category} • $dateString • ${expense.note}" else "${expense.category} • $dateString"
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            
            Text(
                text = "${if(isIncome) "+" else "-"} ${formatRupiah(expense.amount)}",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
            val incomeColor = GraphBarDefault
            val expenseColor = GraphBarActive
            
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
            
            val maxVal = maxOf(income, expense)
            val scaleFactor = if(maxVal > 0) 0.8f / maxVal else 0f
            
            val animatedIncomeScale by androidx.compose.animation.core.animateFloatAsState(
                targetValue = income * scaleFactor,
                animationSpec = androidx.compose.animation.core.tween(1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                label = "income_bar"
            )

            val animatedExpenseScale by androidx.compose.animation.core.animateFloatAsState(
                targetValue = expense * scaleFactor,
                animationSpec = androidx.compose.animation.core.tween(1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                label = "expense_bar"
            )
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (maxVal == 0f) return@Canvas
                
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barWidth = 40.dp.toPx()
                val spacing = 20.dp.toPx()
                
                val startX = (canvasWidth - (barWidth * 2 + spacing)) / 2
                
                // Income Bar
                drawRoundRect(
                    color = incomeColor,
                    topLeft = Offset(startX, canvasHeight - (animatedIncomeScale * canvasHeight)),
                    size = Size(barWidth, animatedIncomeScale * canvasHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
                
                // Expense Bar
                drawRoundRect(
                    color = expenseColor,
                    topLeft = Offset(startX + barWidth + spacing, canvasHeight - (animatedExpenseScale * canvasHeight)),
                    size = Size(barWidth, animatedExpenseScale * canvasHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
            }
            
            // Labels manually overlaid for simplicity
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Pemasukan", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(32.dp))
                Text("Pengeluaran", fontSize = 10.sp, color = GraphBarActive, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(amount).replace("Rp", "Rp ").substringBefore(",")
}

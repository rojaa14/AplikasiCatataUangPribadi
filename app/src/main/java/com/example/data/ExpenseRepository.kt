package com.example.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()

    suspend fun insert(expense: ExpenseEntity) = dao.insertExpense(expense)
    suspend fun delete(expense: ExpenseEntity) = dao.deleteExpense(expense)
}

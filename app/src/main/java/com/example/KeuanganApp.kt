package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.ExpenseRepository

class KeuanganApp : Application() {
    lateinit var database: AppDatabase
    lateinit var repository: ExpenseRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "keuangan_database"
        ).build()
        repository = ExpenseRepository(database.expenseDao())
    }
}

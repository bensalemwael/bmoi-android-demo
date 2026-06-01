package com.bmoi.sifipdemo.data.model

data class BankAccount(
    val holder: String,
    val accountNumberMasked: String,
    val balanceMga: Long,
    val transactions: List<Transaction>,
)

data class Transaction(
    val id: String,
    val label: String,
    val date: String,
    val amountMga: Long, // negative = debit
)

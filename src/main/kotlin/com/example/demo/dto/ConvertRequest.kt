package com.example.demo.dto

data class ConvertRequest (
    val id: String,
    val price: Float,
    val currencies: List<String>,
)
package com.bassem.shoppingadmin.models

import java.sql.Timestamp
import java.util.*

data class OrderClass(
    val order_date: Date? = null,
    val cost: String? = null,
    val status: String? = null,
    val order_id: String? = null,
    val user_id:String?=null
)

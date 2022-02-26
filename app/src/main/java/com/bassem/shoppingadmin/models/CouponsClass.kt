package com.bassem.shoppingadmin.models

data class CouponsClass(
    val title: String? = null,
    val valid: Boolean? = null,
    val isPercentage: Boolean? = null,
    val expireDate: String? = null,
    val usingCount: Int? = null,
    val maxUsers: Int? = null,
    val amount: Int? = null
)

package com.bassem.shoppingadmin.models

data class ItemClass(
    val title: String? = null,
    val photo: String? = null,
    val price: String? = null,
    val amount: Int? = null,
    val id: String? = null,
    var visible: Boolean? = null,
    var category: String? = null

)

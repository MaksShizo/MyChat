package com.lenincompany.mychat.models

data class Contact(
    val name: String,
    val phone: String,
    val userId: Int? = null
)
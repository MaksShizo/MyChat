package com.lenincompany.mychat.models

import kotlinx.serialization.Serializable

@Serializable
data class Token (

    val UserId: Int,

    val RefreshToken: String,

    val AccessToken: String
)

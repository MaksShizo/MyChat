package com.lenincompany.mychat.models.user

data class UserInfoResponse (
    val UserId : Int,
    val CreateDate : String,
    val Name : String,
    val Email : String,
    val Photo : String?,
    val Phone : String
)




package com.paasta.hiclass.model
import java.io.Serializable
//서버에서 오는 데이터 응답 값.

data class UserData(
    val email : String,
    val password: String,
    val name : String,
    val image : String,
    val role : String,

):Serializable
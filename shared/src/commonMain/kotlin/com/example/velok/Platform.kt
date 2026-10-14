package com.example.velok

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
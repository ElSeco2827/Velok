package com.velok.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
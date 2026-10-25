package com.project.lolstudio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
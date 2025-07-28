package com.example.aegisai

import android.app.Application
import com.example.aegisai.network.RetrofitClient

class AegisAiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // This is where you can initialize things that need a context
        // and should only be created once for the entire app lifecycle.

        // Example: Initialize RetrofitClient if it needs context.
        // If your RetrofitClient doesn't need context, this can be empty.
    }
}
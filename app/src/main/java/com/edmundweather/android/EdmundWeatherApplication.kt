package com.edmundweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.security.AccessControlContext

class EdmundWeatherApplication : Application(){
    companion object{
        const val TOKEN = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate(){
        super.onCreate()
        context = applicationContext
    }
}
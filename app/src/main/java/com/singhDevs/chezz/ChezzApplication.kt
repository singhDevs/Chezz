package com.singhDevs.chezz

import android.app.Application
import com.singhDevs.chezz.auth.AuthManager
import com.singhDevs.chezz.viewmodels.AuthViewModel

class ChezzApplication: Application() {
    val authViewModel: AuthViewModel by lazy {
        AuthViewModel(applicationContext)
    }
    fun getAuthManager(): AuthManager = authViewModel.getAuthManager()
}
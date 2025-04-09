package com.singhDevs.chezz.viewmodels

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singhDevs.chezz.auth.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(context: Context) : ViewModel() {
    private val authManager = AuthManager.getInstance(context)
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        viewModelScope.launch(Dispatchers.IO) { // Offload to background
            checkAuthState()
        }
    }

    fun checkAuthState() {
        _authState.postValue(
            if (authManager.isLoggedIn()) AuthState.Authenticated else AuthState.Unauthenticated
        )
    }

    suspend fun signOut(context: Context) {
        authManager.clearCredentials()
        val creds = CredentialManager.create(context)
        creds.clearCredentialState(
            request = ClearCredentialStateRequest()
        )
        // Destroy token on server
    }

    fun getAuthManager(): AuthManager = authManager

    sealed class AuthState {
        data object Authenticated : AuthState()
        data object Unauthenticated : AuthState()
    }
}
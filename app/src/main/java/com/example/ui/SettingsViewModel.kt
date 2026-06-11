package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.KeuanganApp
import com.example.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val theme: StateFlow<String> = repository.theme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sistem")
    val aiProvider: StateFlow<String> = repository.aiProvider.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Gemini")
    val customApiKey: StateFlow<String> = repository.customApiKey.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun setTheme(theme: String) = viewModelScope.launch { repository.setTheme(theme) }
    fun setAiProvider(provider: String) = viewModelScope.launch { repository.setAiProvider(provider) }
    fun setCustomApiKey(key: String) = viewModelScope.launch { repository.setCustomApiKey(key) }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as KeuanganApp
                return SettingsViewModel(application.settingsRepository) as T
            }
        }
    }
}

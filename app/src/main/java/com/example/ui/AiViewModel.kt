package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AiRepository
import com.example.data.ExpenseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AiViewModel(private val aiRepository: AiRepository) : ViewModel() {
    private val _advice = MutableStateFlow<String>("")
    val advice: StateFlow<String> = _advice
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getAdvice(provider: String, apiKey: String, expenses: List<ExpenseEntity>) {
        if (apiKey.isBlank()) {
            _advice.value = "Kunci API belum diatur. Set di tab Saya."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _advice.value = aiRepository.getAdvice(provider, apiKey, expenses)
            _isLoading.value = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AiViewModel(AiRepository()) as T
            }
        }
    }
}

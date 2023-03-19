package com.example.vanatherm.heatinghistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vanatherm.data.HeatingHistoryRepository

class HeatingHistoryModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeatingHistoryViewModel::class.java)) {
            return HeatingHistoryViewModel(HeatingHistoryRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
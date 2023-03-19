package com.example.vanatherm.vanatherm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vanatherm.data.VanaThermDataSource
import com.example.vanatherm.data.VanaThermRepository

class VanaThermModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VanaThermViewModel::class.java)) {
            return VanaThermViewModel(
                vanaThermRepository = VanaThermRepository(
                    vanaThermDataSource = VanaThermDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
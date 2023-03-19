package com.example.vanatherm.data.model

import java.io.Serializable

data class DeviceReadData(
    val temperature: Float = 0F,
    val tempUnit: String = "",
    val humidity: Float = 0F
) : Serializable

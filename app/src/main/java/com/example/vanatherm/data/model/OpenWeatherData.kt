package com.example.vanatherm.data.model

import java.io.Serializable

data class OpenWeatherData(
    val weatherStatus: String = "",
    val icon: String = "",
    val temp: Float = 0F
) : Serializable

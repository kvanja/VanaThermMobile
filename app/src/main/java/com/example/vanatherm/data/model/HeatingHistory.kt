package com.example.vanatherm.data.model

import java.io.Serializable

data class HeatingHistory(
    val id: Int = 0,
    var userId: Int = 0,
    var deviceId: Int = 0,
    val tempMeasurementUnitId: Int = 0,
    val date: String = "",
    val timeFrom: String = "",
    val timeTo: String = "",
    val currentTemperature: Float = 0F,
    var desiredTemperature: Int = 0,
    val created: String = "",
    val updated: String = "",
    val createdBy: Int = 0,
    val updatedBy: Int = 0
) : Serializable

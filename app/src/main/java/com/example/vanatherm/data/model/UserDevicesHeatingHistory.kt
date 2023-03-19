package com.example.vanatherm.data.model

import java.io.Serializable

data class UserDevicesHeatingHistory(
    val buildingName: String = "",
    val city: String = "",
    val street: String = "",
    val floor: Int = 0,
    val roomName: String = "",
    val deviceId: Int = 0,
    val date: String = "",
    val timeFrom: String = "",
    val timeTo: String = "",
    val currentTemperature: Float = 0F,
    val desiredTemperature: Float = 0F,
    val unit: String = ""
) : Serializable

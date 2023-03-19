package com.example.vanatherm.data.model

import java.io.Serializable

data class UserDevicesHeatingHistories(
    val userDevicesHeatingHistory: List<UserDevicesHeatingHistory> = listOf(
        UserDevicesHeatingHistory()
    )
) : Serializable


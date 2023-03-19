package com.example.vanatherm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserBuildingDevices(
    @SerializedName("BUILDINGS")
    val buildings: List<Buildings> = listOf(Buildings())
) : Serializable

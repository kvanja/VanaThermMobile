package com.example.vanatherm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
    @SerializedName("city")
    val city: String
) : Serializable

package com.example.vanatherm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Floors(
    @SerializedName("ID")
    val id: Int = 0,
    @SerializedName("BUILDING_ID")
    val buildingId: Int = 0,
    @SerializedName("FLOOR")
    val floor: Int = 0,
    @SerializedName("DEVICES")
    val devices: Int = 0,
    @SerializedName("CREATED")
    val created: String = "",
    @SerializedName("UPDATED")
    val updated: String = "",
    @SerializedName("CREATED_BY")
    val createdBy: Int = 0,
    @SerializedName("UPDATED_BY")
    val updatedBy: Int = 0,
    @SerializedName("ROOMS")
    val rooms: List<Rooms> = listOf(Rooms())
) : Serializable

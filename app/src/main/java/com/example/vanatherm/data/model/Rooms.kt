package com.example.vanatherm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Rooms(
    @SerializedName("ID")
    val id: Int = 0,
    @SerializedName("BUILDING_ID")
    val buildingId: Int = 0,
    @SerializedName("FLOOR_ID")
    val floordId: Int = 0,
    @SerializedName("ROOM_NAME")
    val roomName: String = "",
    @SerializedName("DEVICES")
    val devices: List<Devices> = listOf(Devices()),
    @SerializedName("CREATED")
    val created: String = "",
    @SerializedName("UPDATED")
    val updated: String = "",
    @SerializedName("CREATED_BY")
    val createdBy: Int = 0,
    @SerializedName("UPDATED_BY")
    val updatedBy: Int = 0
) : Serializable

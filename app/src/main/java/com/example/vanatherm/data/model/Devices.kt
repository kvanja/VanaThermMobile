package com.example.vanatherm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Devices(
    @SerializedName("ID")
    val id: Int = 0,
    @SerializedName("ROOM_ID")
    val roomId: Int = 0,
    @SerializedName("IS_CONTROL_DEVICE")
    val isControlDevice: Boolean = false,
    @SerializedName("IS_CONTROL_ON")
    val isControlOn: Boolean = false,
    @SerializedName("IS_DEVICE_ACTIVE")
    val isDeviceActive: Boolean = false,
    @SerializedName("CREATED")
    val created: String = "",
    @SerializedName("UPDATED")
    val updated: String = "",
    @SerializedName("CREATED_BY")
    val createdBy: Int = 0,
    @SerializedName("UPDATED_BY")
    val updatedBy: Int = 0
) : Serializable

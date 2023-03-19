package com.example.vanatherm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Buildings(
    @SerializedName("ID")
    val id: Int = 0,
    @SerializedName("NAME")
    val name: String = "",
    @SerializedName("STREET")
    val street: String = "",
    @SerializedName("STREET_NUMBER")
    val streetNumber: Int = 0,
    @SerializedName("COUNTRY")
    val country: String = "",
    @SerializedName("CITY")
    val city: String = "",
    @SerializedName("ZIP_CODE")
    val zipCode: Int = 0,
    @SerializedName("LOCATION")
    val location: String = "",
    @SerializedName("CREATED")
    val created: String = "",
    @SerializedName("UPDATED")
    val updated: String = "",
    @SerializedName("CREATED_BY")
    val createdBy: Int = 0,
    @SerializedName("UPDATED_BY")
    val updatedBy: Int = 0,
    @SerializedName("FLOORS")
    val floors: List<Floors> = listOf(Floors())
) : Serializable

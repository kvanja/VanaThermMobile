package com.example.vanatherm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("FIRST_NAME")
        val firstName: String = "",
        @SerializedName("LAST_NAME")
        val lastName: String = "",
        @SerializedName("GENDER")
        val gender: String = "",
        @SerializedName("USERNAME")
        val userName: String = "",
        @SerializedName("PASSWORD")
        val password: String = "",
        @SerializedName("EMAIL")
        val email: String = "",
        @SerializedName("BIRTH_DATE")
        val birthDate: String = "",
        @SerializedName("CREATED")
        val created: String = "",
        @SerializedName("UPDATED")
        val updated: String = "",
        @SerializedName("CREATED_BY")
        val createdBy: Int = 0,
        @SerializedName("UPDATED_BY")
        val updatedBy: Int = 0
) : Serializable
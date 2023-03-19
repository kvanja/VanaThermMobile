package com.example.vanatherm.data

import com.example.vanatherm.apiInteraction.RetrofitInstance
import com.example.vanatherm.data.model.LoggedInUser
import com.example.vanatherm.data.model.UserDevicesHeatingHistories
import retrofit2.Response

class HeatingHistoryRepository {

    suspend fun getUserDevicesHeatingHistory(loggedInUser: LoggedInUser): Response<UserDevicesHeatingHistories> {
        return RetrofitInstance.api.getUserDevicesHeatingHistory(loggedInUser)
    }
}
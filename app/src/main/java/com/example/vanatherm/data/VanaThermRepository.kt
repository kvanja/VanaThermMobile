package com.example.vanatherm.data

import com.example.vanatherm.apiInteraction.RetrofitInstance.api
import com.example.vanatherm.data.model.*
import retrofit2.Response

class VanaThermRepository(vanaThermDataSource: VanaThermDataSource) {

    suspend fun getUserBuildingsAndDevices(user: LoggedInUser): Response<UserBuildingDevices> {
        return api.getUserBuildingsAndDevices(user)
    }

    suspend fun getBuildingCityTemp(city: City): Response<OpenWeatherData> {
        return api.getBuildingCityTemp(city)
    }

    suspend fun getDeviceData(device: Devices): Response<DeviceReadData> {
        return api.getDeviceData(device)
    }

    suspend fun setDesiredTemperatureToDevice(heatingHistory: HeatingHistory): Response<HeatingHistory> {
        return api.setDesiredTemperatureToDevice(heatingHistory)
    }

    suspend fun setRelayStatus(mqttPathToDevice: MQTTPathToDevice): Response<MQTTPathToDevice> {
        return api.setRelayStatus(mqttPathToDevice)
    }
}
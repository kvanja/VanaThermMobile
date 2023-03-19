package com.example.vanatherm.apiInteraction

import com.example.vanatherm.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiMethods {

    @POST("/authUser")
    suspend fun authUser(@Body data: LoggedInUser): Response<LoggedInUser>

    @POST("/getUserBuildingsAndDevices")
    suspend fun getUserBuildingsAndDevices(@Body user: LoggedInUser): Response<UserBuildingDevices>

    @POST("/getOpenWeatherData")
    suspend fun getBuildingCityTemp(@Body city: City): Response<OpenWeatherData>

    @POST("/getDeviceData")
    suspend fun getDeviceData(@Body device: Devices): Response<DeviceReadData>

    @POST("/setDesiredTemperatureToDevice")
    suspend fun setDesiredTemperatureToDevice(@Body heatingHistory: HeatingHistory): Response<HeatingHistory>

    @POST("/setRelayStatus")
    suspend fun setRelayStatus(@Body mqttPathToDevice: MQTTPathToDevice): Response<MQTTPathToDevice>

    @POST("/getHeatingHistory")
    suspend fun getUserDevicesHeatingHistory(@Body loggedInUser: LoggedInUser): Response<UserDevicesHeatingHistories>
}
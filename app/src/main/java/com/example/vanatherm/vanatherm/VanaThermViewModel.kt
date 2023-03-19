package com.example.vanatherm.vanatherm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vanatherm.apiInteraction.ResponseBodyToClass
import com.example.vanatherm.data.VanaThermRepository
import com.example.vanatherm.data.model.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class VanaThermViewModel(private val vanaThermRepository: VanaThermRepository) : ViewModel() {

    private lateinit var loggedInUser: LoggedInUser

    private var btnIncreaseEnabled: Boolean = true
    private var btnDecreaseEnabled: Boolean = false

    private var spinnerBuildingsPos: Int = 0
    private var spinnerFloorsPos: Int = 0
    private var spinnerRoomsPos: Int = 0
    private var spinnerDevicesPos: Int = 0

    private var rotationHappened: Boolean = false

    var mqttPathToDevice: MQTTPathToDevice = MQTTPathToDevice()
    var heatingHistory: HeatingHistory = HeatingHistory()

    private lateinit var device: Devices

    private val _userBuildingDevices = MutableLiveData<UserBuildingDevices>()
    val userBuildingDevices = _userBuildingDevices

    private val _buildingCityTemp = MutableLiveData<OpenWeatherData>()
    val buildingCityTemp = _buildingCityTemp

    private val _deviceReadData = MutableLiveData<DeviceReadData>()
    val deviceReadData = _deviceReadData

    private val _desiredTemperature = MutableLiveData<HeatingHistory>()
    val desiredTemperature = _desiredTemperature

    private val _mqttPathToDevice = MutableLiveData<MQTTPathToDevice>()
    val mqttPathToDeviceR = _mqttPathToDevice

    private val responseBodyToClass: ResponseBodyToClass = ResponseBodyToClass()

    /**
     * Set logged in user
     * @param loggedInUser data of user that is logged in
     */
    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.loggedInUser = loggedInUser
        mqttPathToDevice.username = loggedInUser.userName
        heatingHistory.userId = loggedInUser.id
    }

    /**
     * Sets status to button increase and returns it
     * @param status true means button is enabled, false means it's disabled
     */
    fun changeBtnIncreaseStatus(status: Boolean): Boolean {
        btnIncreaseEnabled = status
        return btnIncreaseEnabled
    }

    /**
     * Sets status to button decrease and returns it
     * @param status true means button is enabled, false means it's disabled
     */
    fun changeBtnDecreaseStatus(status: Boolean): Boolean {
        btnDecreaseEnabled = status
        return btnDecreaseEnabled
    }

    /**
     * Returns true if btnIncrease is enabled, else returns false
     */
    fun getBtnIncreaseStatus(): Boolean {
        return btnIncreaseEnabled
    }

    /**
     * Returns true if btnDecrease is enabled, else returns false
     */
    fun getBtnDecreaseStatus(): Boolean {
        return btnDecreaseEnabled
    }

    /**
     * If screen rotated sets new state to rotationHappened
     * @param happened if there was screen rotation, set it to true, else to false
     */
    fun rotationHappened(happened: Boolean) {
        rotationHappened = happened
    }

    /**
     * Returns true if screen orientation changed, else returns false
     */
    fun getRotationHappened(): Boolean {
        return rotationHappened
    }

    /**
     * Sets position of last selected element Buildings spinner
     * @param pos position of last selected index
     */
    fun setSpinnerBuildingPos(pos: Int) {
        spinnerBuildingsPos = pos
    }

    /**
     * Get position of last selected item of Buildings spinner
     */
    fun getSpinnerBuildingPos(): Int {
        return spinnerBuildingsPos
    }

    /**
     * Sets position of last selected element Floors spinner
     * @param pos position of last selected index
     */
    fun setSpinnerFloorPos(pos: Int) {
        spinnerFloorsPos = pos
    }

    /**
     * Get position of last selected item of Floors spinner
     */
    fun getSpinnerFloorPos(): Int {
        return spinnerFloorsPos
    }

    /**
     * Sets position of last selected element Rooms spinner
     * @param pos position of last selected index
     */
    fun setSpinnerRoomsPos(pos: Int) {
        spinnerRoomsPos = pos
    }

    /**
     * Get position of last selected item of Rooms spinner
     */
    fun getSpinnerRoomsPos(): Int {
        return spinnerRoomsPos
    }

    /**
     * Sets position of last selected element Devices spinner
     * @param pos position of last selected index
     */
    fun setSpinnerDevicesPos(pos: Int) {
        spinnerDevicesPos = pos
    }

    /**
     * Get position of last selected item of Buildings spinner
     */
    fun getSpinnerDevicesPos(): Int {
        return spinnerDevicesPos
    }

    fun setDevice(device: Devices) {
        this.device = device
    }

    /**
     * Gets buildings, it's floors, rooms and devices user has control over
     */
    suspend fun getUserBuildingsAndCtrlDev() {
        withContext(IO) {
            try {
                val response = vanaThermRepository.getUserBuildingsAndDevices(loggedInUser)
                if (response.isSuccessful) {
                    withContext(Main) {
                        val buildings =
                            responseBodyToClass.responseBodyToClass(
                                response,
                                UserBuildingDevices::class.java
                            )
                        _userBuildingDevices.value = buildings
                        Log.d("API", response.toString())
                    }
                } else {
                    Log.d("API", "Api Error 1")
                }
            } catch (e: SocketTimeoutException) {

            }
        }
    }

    /**
     * Gets current weather data of given City
     * @param city City to get weather data for
     */
    suspend fun getBuildingCityTemperature(city: City) {
        withContext(IO) {
            try {
                val response = vanaThermRepository.getBuildingCityTemp(city)
                if (response.isSuccessful) {
                    withContext(Main) {
                        val cityTemp = responseBodyToClass.responseBodyToClass(
                            response,
                            OpenWeatherData::class.java
                        )
                        _buildingCityTemp.value = cityTemp
                        Log.d("API", response.toString())
                    }
                } else {
                    Log.d("API", "Api Error 2")
                }
            } catch (e: SocketTimeoutException) {

            }
        }
    }

    /**
     * Gets device's sensor values(temperature, humidity) and measurement units
     * @param device device to get data for
     */
    suspend fun getDeviceData(device: Devices) {
        withContext(IO) {
            try {
                val response = vanaThermRepository.getDeviceData(device)
                if (response.isSuccessful) {
                    withContext(Main) {
                        val deviceReadData =
                            responseBodyToClass.responseBodyToClass(
                                response,
                                DeviceReadData::class.java
                            )
                        _deviceReadData.value = deviceReadData
                        Log.d("API", response.toString())
                    }
                } else {
                    Log.d("API", "Api Error 3")
                    withContext(Main) {
                        _deviceReadData.value = DeviceReadData(temperature = -100F)
                    }
                }
            } catch (e: SocketTimeoutException) {

            }
        }
    }

    suspend fun setDesiredTempToDevice() {
        withContext(IO) {
            try {
                val response = vanaThermRepository.setDesiredTemperatureToDevice(heatingHistory)
                if (response.isSuccessful) {
                    withContext(Main) {
                        val heatingHistory =
                            responseBodyToClass.responseBodyToClass(
                                response,
                                HeatingHistory::class.java
                            )
                        _desiredTemperature.value = heatingHistory
                        Log.d("API", response.toString())
                    }
                } else {
                    Log.d("API", "Api Error 4")
                }
            } catch (e: SocketTimeoutException) {

            }
        }
    }

    suspend fun setRelayStatus(status: String) {
        mqttPathToDevice.relayOn = status
        withContext(IO) {
            try {
                val response = vanaThermRepository.setRelayStatus(mqttPathToDevice)
                if (response.isSuccessful) {
                    withContext(Main) {
                        val mqttPathToDevice =
                            ResponseBodyToClass().responseBodyToClass(
                                response,
                                MQTTPathToDevice::class.java
                            )
                        _mqttPathToDevice.value = mqttPathToDevice
                        Log.d("API", response.toString())
                    }
                } else {
                    Log.d("API", "Api Error 4")
                }
            } catch (e: SocketTimeoutException) {

            }
        }
    }
}
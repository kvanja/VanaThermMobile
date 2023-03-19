package com.example.vanatherm.heatinghistory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vanatherm.apiInteraction.ResponseBodyToClass
import com.example.vanatherm.data.HeatingHistoryRepository
import com.example.vanatherm.data.model.LoggedInUser
import com.example.vanatherm.data.model.UserDevicesHeatingHistories
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class HeatingHistoryViewModel(private val heatingHistoryRepository: HeatingHistoryRepository) :
    ViewModel() {

    private lateinit var loggedInUser: LoggedInUser
    private val responseBodyToClass: ResponseBodyToClass = ResponseBodyToClass()

    private val _userDevicesHeatingHistory = MutableLiveData<UserDevicesHeatingHistories>()
    val userDevicesHeatingHistory = _userDevicesHeatingHistory

    private var rotationHappened: Boolean = false

    suspend fun getUserBuildingsAndCtrlDev() {
        withContext(IO) {
            try {
                val response =
                    heatingHistoryRepository.getUserDevicesHeatingHistory(loggedInUser = loggedInUser)
                if (response.isSuccessful) {
                    withContext(Main) {
                        val userDevicesHeatingHistory =
                            responseBodyToClass.responseBodyToClass(
                                response,
                                UserDevicesHeatingHistories::class.java
                            )
                        _userDevicesHeatingHistory.value = userDevicesHeatingHistory
                        Log.d("API", response.toString())
                    }
                } else {
                    Log.d("API", "Api Error 1")
                }
            } catch (e: SocketTimeoutException) {

            }
        }
    }

    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.loggedInUser = loggedInUser
    }

    /**
     * If screen rotated sets new state to rotationHappened
     * @param happened if there was screen rotation, set it to true, else to false
     */
    fun screenRotated(happened: Boolean) {
        rotationHappened = happened
    }

    /**
     * Returns true if screen orientation changed, else returns false
     */
    fun getRotationHappened(): Boolean {
        return rotationHappened
    }


}
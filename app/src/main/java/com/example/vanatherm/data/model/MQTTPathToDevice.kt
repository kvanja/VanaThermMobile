package com.example.vanatherm.data.model

data class MQTTPathToDevice(
    var username: String = "",
    var buildingName: String = "",
    var floorId: Int = 0,
    var roomId: Int = 0,
    var deviceId: Int = 0,
    var relayOn: String = ""
)

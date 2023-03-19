package com.example.vanatherm.vanatherm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vanatherm.R
import com.example.vanatherm.data.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "VanaThermFragment"
private const val LOGGED_IN_USER = "loggedInUser"

class VanaThermFragment : Fragment(R.layout.vana_therm_fragment) {

    private lateinit var vanaThermViewModel: VanaThermViewModel

    private lateinit var tempSeekBar: SeekBar

    private lateinit var tvDesiredTemp: TextView
    private lateinit var tvCityTemp: TextView
    private lateinit var tvCity: TextView
    private lateinit var tvWeatherStatus: TextView
    private lateinit var tvTemperatureValue: TextView
    private lateinit var tvHumidityValue: TextView

    private lateinit var btnIncrease: Button
    private lateinit var btnDecrease: Button
    private lateinit var btnSet: Button

    private lateinit var etTemperature: EditText

    private lateinit var spinnerBuildings: Spinner
    private lateinit var spinnerFloors: Spinner
    private lateinit var spinnerRooms: Spinner
    private lateinit var spinnerDevices: Spinner

    private lateinit var ivWeatherIcon: ImageView
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var llCreateAcc: LinearLayout


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vanaThermViewModel = ViewModelProvider(this, VanaThermModelFactory())
            .get(VanaThermViewModel::class.java)

        val sharedPref = activity?.getSharedPreferences("isUserLoggedIn", Context.MODE_PRIVATE)!!
        val editor = sharedPref.edit()
        view.findViewById<Button>(R.id.btnClearPreferences).setOnClickListener {
            editor.clear().apply()
        }

        llCreateAcc = requireActivity().findViewById(R.id.llCreateAcc)
        llCreateAcc.visibility = View.GONE

        bottomNavBar = requireActivity().findViewById(R.id.flBottomBar)
        bottomNavBar.selectedItemId = R.id.miVanaTherm

        vanaThermViewModel.setLoggedInUser(arguments?.getSerializable(LOGGED_IN_USER)!! as LoggedInUser)

        CoroutineScope(IO).launch {
            vanaThermViewModel.getUserBuildingsAndCtrlDev()
        }

        btnIncrease = view.findViewById(R.id.btnIncrease)
        btnDecrease = view.findViewById(R.id.btnDecrease)
        btnSet = view.findViewById(R.id.btnSet)

        tempSeekBar = view.findViewById(R.id.sbAdjustTemp)

        tvDesiredTemp = view.findViewById(R.id.tvDesiredTemp)
        tvCityTemp = view.findViewById(R.id.tvCityTemp)
        tvCity = view.findViewById(R.id.tvCity)
        tvWeatherStatus = view.findViewById(R.id.tvWeatherStatus)
        tvTemperatureValue = view.findViewById(R.id.tvTemperatureValue)
        tvHumidityValue = view.findViewById(R.id.tvHumidityValue)

        etTemperature = view.findViewById(R.id.etTemperature)

        spinnerBuildings = view.findViewById(R.id.spinBuildings)
        spinnerFloors = view.findViewById(R.id.spinFloors)
        spinnerRooms = view.findViewById(R.id.spinRooms)
        spinnerDevices = view.findViewById(R.id.spinDevices)

        ivWeatherIcon = view.findViewById(R.id.ivWeatherIcon)

        btnDecrease.isEnabled = vanaThermViewModel.getBtnDecreaseStatus()

        if (tempSeekBar.progress == 0) {
            tempSeekBar.thumb.setTint(
                ContextCompat.getColor(
                    context?.applicationContext!!,
                    R.color.heating_blue
                )
            )
        }

        btnIncrease.setOnClickListener {
            etTemperature.hint = "${++tempSeekBar.progress}${getString(R.string.degreeSign)}"
            if (tempSeekBar.progress == tempSeekBar.max) {
                it.isEnabled =
                    vanaThermViewModel.changeBtnIncreaseStatus(tempSeekBar.progress != tempSeekBar.max)
            }
            btnDecrease.isEnabled = vanaThermViewModel.changeBtnDecreaseStatus(true)
        }

        btnDecrease.setOnClickListener {
            etTemperature.hint = "${--tempSeekBar.progress}${getString(R.string.degreeSign)}"
            if (tempSeekBar.progress == 0) {
                it.isEnabled = vanaThermViewModel.changeBtnDecreaseStatus(tempSeekBar.progress != 0)
            }
            btnIncrease.isEnabled = vanaThermViewModel.changeBtnIncreaseStatus(true)
        }

        btnSet.setOnClickListener {
            CoroutineScope(IO).launch {
                vanaThermViewModel.heatingHistory.desiredTemperature = tempSeekBar.progress
                vanaThermViewModel.setDesiredTempToDevice()
                if (tempSeekBar.progress > vanaThermViewModel.deviceReadData.value?.temperature?.toInt()!!) {
                    vanaThermViewModel.setRelayStatus("true")
                }
                // TODO : nisam siguran što ću ovdje napraviti budemo vidjeli
                else {
                    vanaThermViewModel.setRelayStatus("false")
                }
            }
        }

        tempSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvDesiredTemp.text = "$progress°"
                if (etTemperature.text.isBlank()) {
                    etTemperature.hint = "${tempSeekBar.progress}${getString(R.string.degreeSign)}"
                }

                if (progress < 18) {
                    tempSeekBar.thumb.setTint(
                        ContextCompat.getColor(
                            context!!,
                            R.color.heating_blue
                        )
                    )
                } else {
                    tempSeekBar.thumb.setTint(
                        ContextCompat.getColor(
                            context!!,
                            R.color.heating_red
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (etTemperature.text.isNotBlank()) {
                    etTemperature.setText(
                        "${tempSeekBar.progress}${getString(R.string.degreeSign)}",
                        TextView.BufferType.EDITABLE
                    )
                }
                btnDecrease.isEnabled =
                    vanaThermViewModel.changeBtnDecreaseStatus(seekBar?.progress != 0)
                btnIncrease.isEnabled =
                    vanaThermViewModel.changeBtnIncreaseStatus(seekBar?.progress != seekBar?.max)
            }
        })

        vanaThermViewModel.buildingCityTemp.observe(viewLifecycleOwner,
            Observer { openWeatherData ->
                openWeatherData ?: return@Observer
                ivWeatherIcon.setImageResource(getImageFromResources(openWeatherData.icon))
                tvCityTemp.text =
                    "${openWeatherData.temp}${vanaThermViewModel.deviceReadData.value?.tempUnit ?: "°C"}"
                tvWeatherStatus.text = openWeatherData.weatherStatus
            })

        vanaThermViewModel.deviceReadData.observe(viewLifecycleOwner,
            Observer { deviceReadData ->
                deviceReadData ?: return@Observer
                val temp: Float = if (deviceReadData.temperature != -100F) {
                    deviceReadData.temperature
                } else {
                    0F
                }
                tvTemperatureValue.text = "${temp}${deviceReadData.tempUnit}"
                tvHumidityValue.text = "${deviceReadData.humidity}%"
            })

        vanaThermViewModel.mqttPathToDeviceR.observe(viewLifecycleOwner,
            Observer { response ->
                response ?: return@Observer
                Log.d(TAG, "pinkek")
                val turnedOn = if (response.relayOn == "true") {
                    "on"
                } else {
                    "off"
                }
                Toast.makeText(view.context, "Heating turned $turnedOn", Toast.LENGTH_LONG).show()
            }
        )

        vanaThermViewModel.userBuildingDevices.observe(
            viewLifecycleOwner,
            Observer { userBuildingDevices ->
                userBuildingDevices ?: return@Observer
                if (userBuildingDevices.buildings.isNotEmpty()) {
                    val buildings: MutableList<String> = mutableListOf()

                    for (building in userBuildingDevices.buildings) {
                        buildings.add("${building.name} (${building.city}, ${building.street}${building.streetNumber})")
                    }

                    spinnerBuildings.adapter = fillSpinner(buildings, spinnerBuildings)

                    var currBuilding = Buildings()
                    var currFloor = Floors()
                    var currRoom = Rooms()
                    var currDevice = Devices()
                    spinnerBuildings.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                var buildingSpinnerPos: Int = position
                                currBuilding =
                                    userBuildingDevices.buildings[buildingSpinnerPos]
                                val floors: MutableList<String> = mutableListOf()

                                buildingSpinnerPos = vanaThermViewModel.getSpinnerBuildingPos()

                                if (vanaThermViewModel.getRotationHappened()) {
                                    currBuilding = userBuildingDevices.buildings[buildingSpinnerPos]
                                    spinnerBuildings.setSelection(buildingSpinnerPos)
                                } else {
                                    vanaThermViewModel.setSpinnerBuildingPos(position)
                                }

                                tvCity.text = currBuilding.city

                                vanaThermViewModel.mqttPathToDevice.buildingName = currBuilding.name

                                for (floor in currBuilding.floors) {
                                    val floorOrdinal = floorOrdinalIndicator(floor)
                                    floors.add(floorOrdinal)
                                }

                                spinnerFloors.adapter = fillSpinner(floors, spinnerFloors)

                                CoroutineScope(IO).launch {
                                    vanaThermViewModel.getBuildingCityTemperature(City(city = currBuilding.city))
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    spinnerFloors.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                currFloor = currBuilding.floors[position]
                                val rooms: MutableList<String> = mutableListOf()
                                val floorPos = vanaThermViewModel.getSpinnerFloorPos()
                                if (vanaThermViewModel.getRotationHappened()) {
                                    currFloor = currBuilding.floors[floorPos]
                                    spinnerFloors.setSelection(floorPos)
                                } else {
                                    vanaThermViewModel.setSpinnerFloorPos(position)
                                }

                                for (room in currFloor.rooms) {
                                    rooms.add(room.roomName)
                                }

                                vanaThermViewModel.mqttPathToDevice.floorId =
                                    currFloor.id
                                spinnerRooms.adapter = fillSpinner(rooms, spinnerRooms)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }

                    spinnerRooms.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                currRoom = currFloor.rooms[position]
                                val devices: MutableList<String> = mutableListOf()
                                val roomPos = vanaThermViewModel.getSpinnerRoomsPos()
                                if (vanaThermViewModel.getRotationHappened()) {
                                    currRoom = currFloor.rooms[roomPos]
                                    spinnerRooms.setSelection(roomPos)
                                } else {
                                    vanaThermViewModel.setSpinnerRoomsPos(position)
                                }
                                for (device in currRoom.devices) {
                                    devices.add(device.id.toString())
                                }
                                vanaThermViewModel.mqttPathToDevice.roomId = currRoom.id
                                spinnerDevices.adapter = fillSpinner(devices, spinnerDevices)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    spinnerDevices.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                currDevice = currRoom.devices[position]
                                val devicePos = vanaThermViewModel.getSpinnerDevicesPos()
                                if (vanaThermViewModel.getRotationHappened()) {
                                    currDevice = currRoom.devices[devicePos]
                                    vanaThermViewModel.rotationHappened(false)
                                    spinnerDevices.setSelection(devicePos)
                                } else {
                                    vanaThermViewModel.setSpinnerDevicesPos(position)
                                }
                                vanaThermViewModel.deviceReadData.value =
                                    DeviceReadData(temperature = 1F)
                                vanaThermViewModel.setDevice(currDevice)

                                vanaThermViewModel.mqttPathToDevice.deviceId = currDevice.id
                                vanaThermViewModel.heatingHistory.deviceId = currDevice.id

                                CoroutineScope(IO).launch {
                                    while (vanaThermViewModel.deviceReadData.value?.temperature != -100F) {
                                        vanaThermViewModel.getDeviceData(currDevice)
                                        delay(60000)
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        btnIncrease.isEnabled = vanaThermViewModel.getBtnIncreaseStatus()
        btnDecrease.isEnabled = vanaThermViewModel.getBtnDecreaseStatus()
    }

    override fun onStop() {
        super.onStop()
        super.onDestroy()
        vanaThermViewModel.setSpinnerBuildingPos(spinnerBuildings.selectedItemPosition)
        vanaThermViewModel.rotationHappened(true)
        vanaThermViewModel.userBuildingDevices.value = null
        vanaThermViewModel.buildingCityTemp.value = null
    }

    /**
     * Fill spinner with values
     * @param data data to fill spinner with
     * @param spinner spinner to fill data with
     */
    private fun fillSpinner(data: MutableList<String>, spinner: Spinner): ArrayAdapter<String> {
        val arrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            view?.context!!,
            android.R.layout.simple_spinner_dropdown_item,
            data
        ) {

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                // set item text size
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)

                // set selected item style
                if (position == spinner.selectedItemPosition) {
                    val color = resources.getString(R.color.heating_blue.toInt())
                    view.background = ColorDrawable(Color.parseColor(color))
                }

                return view
            }
        }
        return arrayAdapter
    }

    /**
     * Set image depending on OpenWeatherData image state
     * @param icon icon from OpenWeatherData
     */
    private fun getImageFromResources(icon: String): Int {
        when (icon) {
            "01d" -> return R.drawable.ic_01d
            "01n" -> return R.drawable.ic_01n
            "02d" -> return R.drawable.ic_02d
            "02n" -> return R.drawable.ic_02n
            "03d" -> return R.drawable.ic_03d
            "03n" -> return R.drawable.ic_03n
            "04d" -> return R.drawable.ic_04d
            "04n" -> return R.drawable.ic_04n
            "09d" -> return R.drawable.ic_09d
            "09n" -> return R.drawable.ic_09n
            "10d" -> return R.drawable.ic_10d
            "10n" -> return R.drawable.ic_10n
            "11d" -> return R.drawable.ic_11d
            "11n" -> return R.drawable.ic_11n
            "13d" -> return R.drawable.ic_13d
            "13n" -> return R.drawable.ic_13n
            "50d" -> return R.drawable.ic_50d
            "50n" -> return R.drawable.ic_50n
            else -> return R.drawable.ic_01d
        }
    }

    /**
     * Returns ordinal indicator of given number (st,nd,rd,th)
     * @param floor floor to return ordinal for
     */
    private fun floorOrdinalIndicator(floor: Floors): String {
        val floorNum: Int
        val floorMod100: Int = floor.floor % 100
        floorNum = if (floorMod100 == 11 || floorMod100 == 12 || floorMod100 == 13) {
            floorMod100
        } else {
            floor.floor % 10
        }

        return when (floorNum) {
            1 -> "${floor.floor}st floor"
            2 -> "${floor.floor}nd floor"
            3 -> "${floor.floor}rd floor"
            else -> "${floor.floor}th floor"
        }
    }

    companion object {
        fun newInstance(loggedInUser: LoggedInUser): VanaThermFragment {
            val args = Bundle().apply {
                putSerializable(LOGGED_IN_USER, loggedInUser)
            }
            return VanaThermFragment().apply { arguments = args }
        }
    }
}
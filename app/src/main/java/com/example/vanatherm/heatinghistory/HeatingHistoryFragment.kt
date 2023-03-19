package com.example.vanatherm.heatinghistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vanatherm.R
import com.example.vanatherm.data.model.LoggedInUser
import com.example.vanatherm.data.model.UserDevicesHeatingHistories
import com.example.vanatherm.data.model.UserDevicesHeatingHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

private const val LOGGED_IN_USER = "loggedInUser"

class HeatingHistoryFragment : Fragment(R.layout.heating_history_fragment) {

    private lateinit var heatingHistoryViewModel: HeatingHistoryViewModel
    private lateinit var rvHeatingHistory: RecyclerView
    private var adapter: HeatingHistoryAdapter? = HeatingHistoryAdapter(emptyList())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        heatingHistoryViewModel = ViewModelProvider(this, HeatingHistoryModelFactory())
            .get(HeatingHistoryViewModel::class.java)
        heatingHistoryViewModel.setLoggedInUser(arguments?.getSerializable(LOGGED_IN_USER)!! as LoggedInUser)


        CoroutineScope(IO).launch {
            if (!heatingHistoryViewModel.getRotationHappened()) {
                heatingHistoryViewModel.getUserBuildingsAndCtrlDev()
            } else {
                heatingHistoryViewModel.screenRotated(false)
            }
        }

        heatingHistoryViewModel.userDevicesHeatingHistory.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                updateUI(result)
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.heating_history_fragment, container, false)
        rvHeatingHistory = view.findViewById(R.id.heatinHistory) as RecyclerView
        rvHeatingHistory.layoutManager = LinearLayoutManager(context)
        rvHeatingHistory.adapter = adapter
        return view
    }

    private fun updateUI(userDevicesHeatingHistories: UserDevicesHeatingHistories) {
        adapter = HeatingHistoryAdapter(userDevicesHeatingHistories.userDevicesHeatingHistory)
        rvHeatingHistory.adapter = adapter
    }

    private inner class HeatingHistoryHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var userDevicesHeatingHistory: UserDevicesHeatingHistory
        private val tvBuildingName: TextView = itemView.findViewById(R.id.tvBuildingName)
        private val rvTvCity: TextView = itemView.findViewById(R.id.rvTvCity)
        private val tvStreet: TextView = itemView.findViewById(R.id.tvStreet)
        private val tvFloor: TextView = itemView.findViewById(R.id.tvFloor)
        private val tvRoomName: TextView = itemView.findViewById(R.id.tvRoomName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTimeFrom: TextView = itemView.findViewById(R.id.tvTimeFrom)
        private val tvTimeTo: TextView = itemView.findViewById(R.id.tvTimeTo)
        private val tvAtmTemperature: TextView = itemView.findViewById(R.id.tvAtmTemperature)
        private val tvDesiredTemp: TextView = itemView.findViewById(R.id.tvDesiredTemp)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(userDevicesHeatingHistory: UserDevicesHeatingHistory) {
            this.userDevicesHeatingHistory = userDevicesHeatingHistory
            tvBuildingName.text = userDevicesHeatingHistory.buildingName
            rvTvCity.text = "${getString(R.string.city)}: ${userDevicesHeatingHistory.city}"
            tvStreet.text = "${getString(R.string.street)} ${userDevicesHeatingHistory.street}"
            tvFloor.text = "${getString(R.string.floor)} ${userDevicesHeatingHistory.floor}"
            tvRoomName.text =
                "${getString(R.string.roomName)} ${userDevicesHeatingHistory.roomName}"
            tvDate.text = "${getString(R.string.date)} ${userDevicesHeatingHistory.date}"
            tvTimeFrom.text =
                "${getString(R.string.timeFrom)} ${userDevicesHeatingHistory.timeFrom}"
            tvTimeTo.text = "${getString(R.string.timeTo)} ${userDevicesHeatingHistory.timeTo}"
            tvAtmTemperature.text =
                "${getString(R.string.atmTemperature)} ${userDevicesHeatingHistory.currentTemperature}"
            tvDesiredTemp.text =
                "${getString(R.string.desiredTemperature)} ${userDevicesHeatingHistory.desiredTemperature}"

        }

        override fun onClick(v: View?) {}
    }

    private inner class HeatingHistoryAdapter(val userDevicesHeatingHistories: List<UserDevicesHeatingHistory>) :
        RecyclerView.Adapter<HeatingHistoryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeatingHistoryHolder {
            val view = layoutInflater.inflate(R.layout.fragment_heating_history_item, parent, false)
            return HeatingHistoryHolder(view)
        }


        override fun getItemCount(): Int {
            return userDevicesHeatingHistories.size
        }

        override fun onBindViewHolder(heatingHistoryHolder: HeatingHistoryHolder, position: Int) {
            val item: UserDevicesHeatingHistory = userDevicesHeatingHistories[position]
            heatingHistoryHolder.bind(item)
        }
    }

    override fun onStop() {
        super.onStop()
        heatingHistoryViewModel.screenRotated(true)
    }

    companion object {
        fun newInstance(loggedInUser: LoggedInUser): HeatingHistoryFragment {
            val args = Bundle().apply {
                putSerializable(LOGGED_IN_USER, loggedInUser)
            }
            return HeatingHistoryFragment().apply { arguments = args }
        }
    }

}
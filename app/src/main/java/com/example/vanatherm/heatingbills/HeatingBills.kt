package com.example.vanatherm.heatingbills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vanatherm.R

class HeatingBills : Fragment() {

    companion object {
        fun newInstance() = HeatingBills()
    }

    private lateinit var viewModel: HeatingBillsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.heating_bills_fragment, container, false)
    }

}
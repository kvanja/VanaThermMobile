package com.example.vanatherm

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vanatherm.data.model.LoggedInUser
import com.example.vanatherm.heatingbills.HeatingBills
import com.example.vanatherm.heatinghistory.HeatingHistoryFragment
import com.example.vanatherm.ui.login.LoginFragment
import com.example.vanatherm.vanatherm.VanaThermFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

private val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), LoginFragment.Callbacks {

    private var fragmentContainerId: Int = R.id.flFragmentContainer
    private lateinit var fragment: Fragment
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavBar = findViewById(R.id.flBottomBar)
        mainViewModel = ViewModelProvider(this, MainModelFactory())
            .get(MainViewModel::class.java)

        bottomNavBar.setOnNavigationItemSelectedListener {
            goToNavBarFragmentSelected(it.itemId)
            true
        }

        val currentFragment = supportFragmentManager.findFragmentById(fragmentContainerId)
        fragment = LoginFragment()

        if (currentFragment == null) {
            supportFragmentManager.beginTransaction().replace(fragmentContainerId, fragment)
                .commit()
        }
    }

    override fun onLogin(loggedInUser: LoggedInUser) {
        fragment = VanaThermFragment.newInstance(loggedInUser)
        mainViewModel.setLoggedInUser(loggedInUser)
        bottomNavBar.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(fragmentContainerId, fragment).commit()
    }

    fun goToNavBarFragmentSelected(id: Int) {
        if (id != mainViewModel.getLastBottomNavId()) {
            val fragment =
                when (id) {
                    R.id.miHeatingHistory -> HeatingHistoryFragment.newInstance(mainViewModel.getLoggedInUser())
                    R.id.miHeatingBills -> HeatingBills()
                    R.id.miVanaTherm -> VanaThermFragment.newInstance(mainViewModel.getLoggedInUser())
                    else -> null
                }

            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(fragmentContainerId, fragment)
                    .addToBackStack(null).commit()
                mainViewModel.setLastBottomNavId(id)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState() called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState() called")
    }
}

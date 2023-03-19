package com.example.vanatherm

import androidx.lifecycle.ViewModel
import com.example.vanatherm.data.model.LoggedInUser

class MainViewModel : ViewModel() {
    private lateinit var loggedInUser: LoggedInUser
    private var lastNavId: Int = R.id.miVanaTherm

    /**
     * Set logged in user
     * @param loggedInUser user that's logged in
     */
    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.loggedInUser = loggedInUser
    }

    /**
     * Returns logged in user
     */
    fun getLoggedInUser(): LoggedInUser {
        return this.loggedInUser
    }

    /**
     * Set id of currently selected bottom navigation item
     * @param id id of currently selected bottom navigation item
     */
    fun setLastBottomNavId(id: Int) {
        lastNavId = id
    }

    /**
     * Returns id of currently selected bottom navigation item
     */
    fun getLastBottomNavId(): Int {
        return lastNavId
    }
}
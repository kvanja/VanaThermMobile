package com.example.vanatherm.data

import com.example.vanatherm.R
import com.example.vanatherm.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(
        username: String,
        password: String,
        loggedInUser: LoggedInUser
    ): Result<LoggedInUser> {
        try {
            if (username.isBlank() || password.isBlank()) {
                return Result.Error(IOException(R.string.login_data_empty.toString()))
            } else if ((username.equals(
                    loggedInUser.userName,
                    ignoreCase = true
                ) || username.equals(
                    loggedInUser.email,
                    ignoreCase = true
                )) && loggedInUser.password == password
            ) {
                return Result.Success(loggedInUser)
            }
            return Result.Error(IOException(R.string.login_failed.toString()))
        } catch (e: Throwable) {
            return Result.Error(IOException(R.string.error.toString(), e))
        }
    }


    fun logout() {
        // TODO: revoke authentication
    }
}
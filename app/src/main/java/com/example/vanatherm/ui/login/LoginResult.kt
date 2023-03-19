package com.example.vanatherm.ui.login

import java.net.SocketTimeoutException

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null,
        val timeoutError: SocketTimeoutException? = null,
        val errorStr: String? = null
)
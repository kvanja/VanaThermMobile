package com.example.vanatherm.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vanatherm.R
import com.example.vanatherm.data.LoginRepository
import com.example.vanatherm.data.Result
import com.example.vanatherm.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginAuth = MutableLiveData<Response<LoggedInUser>>()
    val loginAuth: LiveData<Response<LoggedInUser>> = _loginAuth

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private lateinit var username: String
    private lateinit var password: String
    private lateinit var loggedInUser: LoggedInUser

    /**
     * Authenticate user for login
     * @param authUsername username
     * @param authPwd password
     */
    suspend fun authUser(authUsername: String, authPwd: String) {
        // set input variables each time someone clicks on login button
        username = authUsername
        password = authPwd
        // setting up variables needs to be on main thread, not on background thread
        withContext(IO) {
            try {
                val response = loginRepository.authUser(
                    LoggedInUser(
                        userName = username,
                        password = password
                    )
                )
                if (response.isSuccessful) {
                    withContext(Main) {
                        _loginAuth.value = response
                    }
                } else {
                    withContext(Main) {
                        _loginResult.value = LoginResult(error = R.string.login_failed)
                    }
                }
            } catch (e: SocketTimeoutException) {
                withContext(Main) {
                    _loginResult.value = LoginResult(error = R.string.api_error, timeoutError = e)
                }
            }
        }
    }

    /**
     * Save logged in user data
     * @param loggedInUser logged in user
     */
    fun login(loggedInUser: LoggedInUser) {
        // authenticate user and return result in suspend function that uses coroutine
        this.loggedInUser = loggedInUser
        val result = loginRepository.login(username, password, loggedInUser)
        // setting up variables needs to be on main thread, not on background thread
        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = "${result.data.firstName} ${result.data.lastName}"))
        } else if (result is Result.Error) {
            _loginResult.value = LoginResult(error = result.exception.message?.toInt())
        }
    }

    /**
     *  Check user data after it was changed
     */
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }

    fun getLoggedInUser(): LoggedInUser {
        return this.loggedInUser
    }

}
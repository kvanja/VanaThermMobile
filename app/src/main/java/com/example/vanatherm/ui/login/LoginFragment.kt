package com.example.vanatherm.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vanatherm.R
import com.example.vanatherm.data.model.LoggedInUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class LoginFragment : Fragment(R.layout.fragment_login) {

    private var callbacks: Callbacks? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var llCreateAcc: LinearLayout

    interface Callbacks {
        fun onLogin(loggedInUser: LoggedInUser)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init viewModel (by lazy iz deprecated)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        etUsername = view.findViewById(R.id.tietEmail)
        etPassword = view.findViewById(R.id.tietPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        loadingProgressBar = view.findViewById(R.id.loading)

        bottomNavBar = requireActivity().findViewById(R.id.flBottomBar)
        bottomNavBar.visibility = View.GONE
        llCreateAcc = requireActivity().findViewById(R.id.llCreateAcc)
        llCreateAcc.visibility = View.VISIBLE

        sharedPref = activity?.getSharedPreferences("isUserLoggedIn", Context.MODE_PRIVATE)!!
        editor = sharedPref.edit()
        editor.apply()

        autoLoginUser(view)

        //handles login form (makes sure correct data is entered)
        loginViewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                btnLogin.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    etUsername.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    etPassword.error = getString(it)
                    //coroutine that clears error on password edit text after 3 seconds
                    CoroutineScope(IO).launch {
                        clearETerror(etPassword, 3000)
                    }
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                btnLogin.isEnabled = false
                loginResult.errorStr?.let {
                    showLoginFailed(it)
                }
                loginResult.timeoutError?.let {
                    showLoginFailed(it.message.toString())
                    CoroutineScope(IO).launch {
                        loginViewModel.authUser(
                            etUsername.text.toString(),
                            etPassword.text.toString()
                        )
                    }
                    loadingProgressBar.visibility = View.VISIBLE
                }
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                    goToVanatherm()
                }
            })

        loginViewModel.loginAuth.observe(viewLifecycleOwner,
            Observer { response ->
                response ?: return@Observer
                if (response.isSuccessful) {
                    val gson = Gson()
                    val jsonBody = gson.toJson(response.body())
                    val loggedInUser = gson.fromJson(jsonBody.toString(), LoggedInUser::class.java)
                    loginViewModel.login(loggedInUser)
                    editor.apply {
                        putString("etUsername", etUsername.text.toString())
                        putString("etPassword", etPassword.text.toString())
                        apply()
                    }
                }
            })

        // checking if text in EditText views has changed
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            // check validity of EditText's
            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    etUsername.text.toString(),
                    etPassword.text.toString()
                )
            }
        }

        // adding listeners to EditText fields
        etUsername.addTextChangedListener(afterTextChangedListener)
        etPassword.addTextChangedListener(afterTextChangedListener)
        // sets action listener for password ET and and it's done check (in keyboard)
        etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.visibility = View.VISIBLE
                CoroutineScope(IO).launch {
                    loginViewModel.authUser(
                        etUsername.text.toString(),
                        etPassword.text.toString()
                    )
                }
            }
            false
        }
        // login button listener
        btnLogin.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            CoroutineScope(IO).launch {
                loginViewModel.authUser(
                    etUsername.text.toString(),
                    etPassword.text.toString()
                )
            }
        }

    }

    /**
     * Makes toast to show user login successful message
     * @param model needed to display user's name
     */
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    /**
     * Makes toast to show login failed message to user
     * @param errorString resource id of string to show user
     */
    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(errorString: String) {
        Log.d("API", errorString)
    }

    /**
     * Clears error off of given EditText after given delay
     * @param et edit text to clear error on
     */
    private suspend fun clearETerror(et: EditText, delay: Long) {
        withContext(IO) {
            delay(delay)
            withContext(Main) {
                et.error = null
            }
        }
    }

    /**
     * After user is logged in go to VanaTherm fragment
     */
    private fun goToVanatherm() {
        callbacks?.onLogin(loginViewModel.getLoggedInUser())
    }

    /**
     * If user was already logged in, but the application was closed, hide all elements except loadingSeekBar and VanaTherm icon and login user again
     */
    private fun autoLoginUser(view: View) {
        // if there is saved username and password to shared preferences, set edit text's to saved values
        etUsername.setText(sharedPref.getString("etUsername", ""))
        etPassword.setText(sharedPref.getString("etPassword", ""))
        // if no data is entered in loginForm we want to prevent user to click on Log In button
        btnLogin.isEnabled = false
        if (etUsername.text.isNotBlank()) {
            loadingProgressBar.visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.llInputs).visibility = View.INVISIBLE
            requireActivity().findViewById<LinearLayout>(R.id.llCreateAcc).visibility =
                View.INVISIBLE
            CoroutineScope(IO).launch {
                loginViewModel.authUser(
                    etUsername.text.toString(),
                    etPassword.text.toString()
                )
            }
        }
    }

}
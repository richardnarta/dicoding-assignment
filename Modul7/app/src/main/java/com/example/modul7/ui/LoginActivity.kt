package com.example.modul7.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.example.modul7.R
import com.example.modul7.databinding.ActivityLoginBinding
import com.example.modul7.utils.CustomEditText
import com.example.modul7.utils.Event
import com.example.modul7.utils.StatusResult
import com.example.modul7.viewmodel.UserStatusViewModel
import com.example.modul7.viewmodel.UserViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val factory: UserViewModelFactory = UserViewModelFactory.getInstance(this)
    private val viewModel: UserStatusViewModel by viewModels {
        factory
    }

    private lateinit var inputEmailLayout: TextInputLayout
    private lateinit var inputEmail: CustomEditText
    private lateinit var inputPasswordLayout: TextInputLayout
    private lateinit var inputPassword: CustomEditText
    private lateinit var loginButton: Button
    private lateinit var goToRegister: TextView
    private lateinit var progressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            inputEmailLayout = firstInput
            inputEmail = edLoginEmail
            inputPasswordLayout = secondInput
            inputPassword = edLoginPassword
            loginButton = btnLogin
            goToRegister = tvRegister
            progressBar = progressBarLogin
        }

        checkBundle()
        validateLogin()

        goToRegister.setOnClickListener {
            moveToActivity(RegisterActivity::class.java)
        }
    }

    private fun checkBundle() {
        try {
            val registrationDetail = intent.getStringArrayListExtra(REGISTRATION)

            if (registrationDetail!!.isNotEmpty()) {
                inputEmail.setText(registrationDetail[0])
                inputPassword.setText(registrationDetail[1])
            }
        }catch (_: Exception) {
        }
    }

    private fun validateLogin() {
        loginButton.setOnClickListener {
            if (inputEmail.text!!.isEmpty() && inputPassword.text!!.isEmpty()) {
                viewModel.snackBarText.value = Event(resources.getString(R.string.all_field_empty))
                showToast()
            } else if (inputEmailLayout.error == null && inputPasswordLayout.error == null) {
                val userEmail = inputEmail.text.toString()
                val userPassword = inputPassword.text.toString()

                viewModel.loginToAccount(userEmail, userPassword).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is StatusResult.Loading -> {
                                progressBar.show()
                            }

                            is StatusResult.Success -> {
                                progressBar.hide()
                                moveToActivity(MainActivity::class.java, true)
                                viewModel.snackBarText.value = Event(resources.getString(R.string.login_success))
                                showToast()
                            }

                            is StatusResult.Error -> {
                                progressBar.hide()
                                viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, result.error))
                                showToast()
                            }
                        }
                    }
                }
            } else {
                if (inputEmailLayout.error != null) {
                    viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, inputEmailLayout.error))
                    showToast()
                } else {
                    viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, inputPasswordLayout.error))
                    showToast()
                }
            }
        }
    }

    private fun moveToActivity(cls: Class<*>, clear: Boolean = false) {
        val move = Intent(this, cls)
        if (clear) {
            move.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(move)
    }

    private fun showToast() {
        viewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REGISTRATION = "registration"
    }
}
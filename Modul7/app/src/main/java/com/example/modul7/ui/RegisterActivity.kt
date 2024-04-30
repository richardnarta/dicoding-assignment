package com.example.modul7.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.example.modul7.R
import com.example.modul7.databinding.ActivityRegisterBinding
import com.example.modul7.utils.CustomEditText
import com.example.modul7.utils.Event
import com.example.modul7.utils.StatusResult
import com.example.modul7.viewmodel.UserStatusViewModel
import com.example.modul7.viewmodel.UserViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val factory: UserViewModelFactory = UserViewModelFactory.getInstance(this)
    private val viewModel: UserStatusViewModel by viewModels {
        factory
    }

    private lateinit var inputNameLayout: TextInputLayout
    private lateinit var inputName: EditText
    private lateinit var inputEmailLayout: TextInputLayout
    private lateinit var inputEmail: CustomEditText
    private lateinit var inputPasswordLayout: TextInputLayout
    private lateinit var inputPassword: CustomEditText
    private lateinit var registerButton: Button
    private lateinit var goToLogin: TextView
    private lateinit var progressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            inputNameLayout = firstInput
            inputName = edRegisterName
            inputEmailLayout = secondInput
            inputEmail = edRegisterEmail
            inputPasswordLayout = thirdInput
            inputPassword = edRegisterPassword
            registerButton = btnRegister
            goToLogin = tvLogin
            progressBar = progressBarRegister
        }

        validateRegister()

        goToLogin.setOnClickListener {
            moveToActivity(LoginActivity::class.java)
        }
    }

    private fun validateRegister() {
        registerButton.setOnClickListener {
            if (inputName.text!!.isEmpty() && inputEmail.text!!.isEmpty() && inputPassword.text!!.isEmpty()) {
                viewModel.snackBarText.value = Event(resources.getString(R.string.all_field_empty))
                showToast()
            } else if (inputNameLayout.error == null && inputEmailLayout.error == null && inputPasswordLayout.error == null) {
                val userName = inputName.text.toString()
                val userEmail = inputEmail.text.toString()
                val userPassword = inputPassword.text.toString()

                viewModel.registerAccount(userName, userEmail, userPassword).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is StatusResult.Loading -> {
                                progressBar.show()
                            }

                            is StatusResult.Success -> {
                                progressBar.hide()

                                val registrationDetail = arrayListOf(userEmail, userPassword)

                                moveToActivity(LoginActivity::class.java, true, registrationDetail)
                                viewModel.snackBarText.value = Event(resources.getString(R.string.register_success))
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
                if (inputNameLayout.error != null) {
                    viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, inputNameLayout.error))
                    showToast()
                } else if (inputEmailLayout.error != null) {
                    viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, inputEmailLayout.error))
                    showToast()
                } else {
                    viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, inputEmailLayout.error))
                    showToast()
                }
            }
        }
    }

    private fun moveToActivity(cls: Class<*>, clear: Boolean = false, data: ArrayList<String> = arrayListOf()) {
        val move = Intent(this, cls)
        if (clear) {
            move.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            move.putStringArrayListExtra(LoginActivity.REGISTRATION, data)
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
}
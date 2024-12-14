package com.lenincompany.mychat.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.ActivityLoginBinding
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.user.UserInfoResponse
import com.lenincompany.mychat.network.TokenRefresher
import com.lenincompany.mychat.ui.auth.forgotpass.ForgotPasswordActivity
import com.lenincompany.mychat.ui.auth.register.RegisterActivity
import com.lenincompany.mychat.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var dataRepository: DataRepository
    private val loginViewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email,password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun setupObservers() {
        loginViewModel.login.observe(this) { login ->
            setupTokenRefresher(login)
        }

        loginViewModel.infoForUser.observe(this) { info ->
            saveUserInfo(info)
        }

        loginViewModel.errorMessage.observe(this) { errorMessage ->
            Log.e("ChatsFragment", "Error: $errorMessage")
        }
    }

    fun saveUserInfo(userInfoResponse: UserInfoResponse) {
        sharedPrefs.saveUser(userInfoResponse)
    }

    fun setupTokenRefresher(token : Token) {
        val sharedPrefs = SharedPrefs(this)
        sharedPrefs.saveTokens(token)
        loginViewModel.getInfoForUser(token.UserId)
        TokenRefresher(dataRepository, SharedPrefs(this))
        startActivity(Intent(this, MainActivity::class.java))
    }
}

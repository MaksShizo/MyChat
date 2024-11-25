package com.lenincompany.mychat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.TokenManager
import com.lenincompany.mychat.network.TokenRefresher
import com.lenincompany.mychat.ui.chats.ChatsActivity
import dagger.android.AndroidInjection
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class LoginActivity : MvpAppCompatActivity(), LoginView {
    @Inject
    lateinit var dataRepository: DataRepository

    @InjectPresenter
    lateinit var presenter: LoginPresenter
    @ProvidePresenter
    fun providePresenter() = LoginPresenter(dataRepository)


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                presenter.login(email,password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val registerButton = findViewById<TextView>(R.id.tvRegister)
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val forgotPasswordButton = findViewById<TextView>(R.id.tvForgotPassword)
        forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    override fun setupTokenRefresher() {
        TokenRefresher(dataRepository, TokenManager(this))
        startActivity(Intent(this, ChatsActivity::class.java))
    }
}

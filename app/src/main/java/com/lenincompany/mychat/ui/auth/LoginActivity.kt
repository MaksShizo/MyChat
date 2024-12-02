package com.lenincompany.mychat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.ActivityLoginBinding
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.user.UserInfoResponse
import com.lenincompany.mychat.network.TokenRefresher
import com.lenincompany.mychat.ui.main.MainActivity
import dagger.android.AndroidInjection
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class LoginActivity : MvpAppCompatActivity(), LoginView {
    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var binding: ActivityLoginBinding
    @InjectPresenter
    lateinit var presenter: LoginPresenter
    @ProvidePresenter
    fun providePresenter() = LoginPresenter(dataRepository)


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                presenter.login(email,password)
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

    override fun saveUserInfo(userInfoResponse: UserInfoResponse) {
        sharedPrefs.saveUser(userInfoResponse)
    }

    override fun setupTokenRefresher(token : Token) {
        val sharedPrefs = SharedPrefs(this)
        sharedPrefs.saveTokens(token)
        presenter.getInfoForUser(token.UserId)
        TokenRefresher(dataRepository, SharedPrefs(this))
        startActivity(Intent(this, MainActivity::class.java))
    }
}

package com.lenincompany.mychat.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.databinding.ActivityRegisterBinding
import dagger.android.AndroidInjection
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class RegisterActivity : MvpAppCompatActivity(), RegisterView {
    @Inject
    lateinit var dataRepository: DataRepository
    private lateinit var binding: ActivityRegisterBinding
    @InjectPresenter
    lateinit var presenter: RegisterPresenter
    @ProvidePresenter
    fun providePresenter() = RegisterPresenter(dataRepository)


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && name.isNotEmpty()) {
                if (password == confirmPassword) {
                    presenter.register(name,email,password)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun confirmRegister() {
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
        finish()
    }

}

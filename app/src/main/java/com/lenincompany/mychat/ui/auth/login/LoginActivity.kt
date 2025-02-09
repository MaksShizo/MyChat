package com.lenincompany.mychat.ui.auth.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.ActivityLoginBinding
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.user.UserInfoResponse
import com.lenincompany.mychat.network.TokenRefresher
import com.lenincompany.mychat.ui.auth.forgotpass.ForgotPasswordActivity
import com.lenincompany.mychat.ui.auth.register.RegisterActivity
import com.lenincompany.mychat.ui.main.MainActivity
import com.lenincompany.mychat.ui.theme.MyChatTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    @Inject
    lateinit var dataRepository: DataRepository
    private val loginViewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var binding: ActivityLoginBinding


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContent {
            MyChatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    LoginScreen()
                }
            }
        }
        binding.btnLogin.setOnClickListener {

        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    fun onLoginButtonClick()
    {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginViewModel.login(email,password)
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
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

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Функция для обработки клика по кнопке
    fun onLoginButtonClick() {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginViewModel.login(email, password)
        } else {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = { onLoginButtonClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Login")
        }

        Text(
            text = "Forgot password?",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Register",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MyChatTheme {
        LoginScreen()
    }
}


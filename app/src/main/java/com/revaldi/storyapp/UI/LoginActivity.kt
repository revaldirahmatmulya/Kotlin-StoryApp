package com.revaldi.storyapp.UI

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.revaldi.storyapp.Api.ApiConfig.instanceRetrofit
import com.revaldi.storyapp.Models.LoginData
import com.revaldi.storyapp.Helper.PreferenceManager
import com.revaldi.storyapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@Suppress("NAME_SHADOWING")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager

    companion object {
        fun getLaunchService(from: Context) = Intent(from, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)

        binding.btnLogin.setOnClickListener {
            if(binding.etEmail.text.toString().isEmpty() || binding.etPassword.text.toString().isEmpty()){
                Toast.makeText(this, "Please fill all the field", Toast.LENGTH_SHORT).show()
            } else {
                    login()

            }

        }
        setupView()
        playAnimation()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.previewImageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    fun login(){
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            val loginData = LoginData(email, password)
            Toast.makeText(this, "Connecting to server...", Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = instanceRetrofit.loginUser(loginData)
                    if (!response.error) {
                        val loginResult = response.loginResult
                        Log.e("Response",loginResult.name)
                        val name = response.loginResult.name
                        val userId = response.loginResult.userId
                        val token = response.loginResult.token
                        preferenceManager.saveLoginData(userId,name,token)
                        val tokenGet = preferenceManager.getToken().toString()
                        Log.e("Token",tokenGet)
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(HomeActivity.getLaunchService(this@LoginActivity)))
                    } else {
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
package com.revaldi.storyapp.UI

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.revaldi.storyapp.Api.ApiConfig.instanceRetrofit
import com.revaldi.storyapp.Models.UserData
import com.revaldi.storyapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    companion object {
        fun getLaunchService(from: Context) = Intent(from, RegisterActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnRegister.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                register()

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
    suspend fun register(){
        if(binding.etName.text.toString().isEmpty() || binding.etEmail.text.toString().isEmpty() || binding.etPassword.text.toString().isEmpty()){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }else{
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            val userData = UserData(name, email, password)
            Toast.makeText(this, "Valid", Toast.LENGTH_SHORT).show()

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = instanceRetrofit.registerUser(userData)
                    if(!response.error){
                        Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(LoginActivity.getLaunchService(this@RegisterActivity)))
                    }else {
                        Toast.makeText(this@RegisterActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, "Error ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

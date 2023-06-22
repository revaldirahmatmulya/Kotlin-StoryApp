package com.revaldi.storyapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.revaldi.storyapp.Helper.PreferenceManager
import com.revaldi.storyapp.UI.HomeActivity
import com.revaldi.storyapp.UI.LoginActivity
import com.revaldi.storyapp.UI.RegisterActivity
import com.revaldi.storyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        sharedPreferencesManager = PreferenceManager(this)
        if(sharedPreferencesManager.isLoggedIn()) {
            startActivity(Intent(HomeActivity.getLaunchService(this@MainActivity)))
            Log.e("TAG", "onCreate: ${sharedPreferencesManager.getName()}")
            Log.e("TAG", "onCreate: ${sharedPreferencesManager.getUserId()}")
        } else {
            setContentView(view)
            binding.loginButton.setOnClickListener {
                startActivity(LoginActivity.getLaunchService(this))
            }
            binding.registerButton.setOnClickListener {
                startActivity(RegisterActivity.getLaunchService(this))
            }
            setupView()
            playAnimation()
        }
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



}

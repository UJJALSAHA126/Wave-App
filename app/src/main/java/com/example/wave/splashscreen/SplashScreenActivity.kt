package com.example.wave.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.wave.MainActivity
import com.example.wave.databinding.ActivitySplashScreenBinding
import com.example.wave.loginactivity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        GlobalScope.launch(Dispatchers.IO) {
            delay(2000)

            val intent = if (auth.currentUser == null) {
                Intent(this@SplashScreenActivity, LoginActivity::class.java)
            } else {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            }

            startActivity(intent)
            finish()
        }

    }
}
package com.example.cookbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cookbook.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this@SplashActivity, SignupActivity::class.java))
            finish()
        }

        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

    }
}
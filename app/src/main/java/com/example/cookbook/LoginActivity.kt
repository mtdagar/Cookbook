package com.example.cookbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.cookbook.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        login()
    }

    private fun login() {

        binding.btnLogin.setOnClickListener{

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(TextUtils.isEmpty(email)) {
                binding.etEmail.error = "Enter your email"
                return@setOnClickListener
            }else if(TextUtils.isEmpty(password)) {
                binding.etPassword.error = "Enter your password"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this@LoginActivity, "Login error! Check email and password", Toast.LENGTH_SHORT).show()
                        }
                    }

        }

    }
}
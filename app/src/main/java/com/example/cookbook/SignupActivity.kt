package com.example.cookbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.cookbook.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        register()

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun register(){

        binding.btnRegister.setOnClickListener{

            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            //basic checks
            if(TextUtils.isEmpty(username)){
                binding.etUsername.error = "Enter your username"
                return@setOnClickListener
            }else if(TextUtils.isEmpty(email)) {
                binding.etEmail.error = "Enter your email"
                return@setOnClickListener
            }else if(TextUtils.isEmpty(password)) {
                binding.etPassword.error = "Enter your password"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            val currentUser = auth.currentUser
                            val currentUserDb = databaseReference?.child((currentUser?.uid!!))
                            currentUserDb?.child("username")?.setValue(username)

                            Toast.makeText(this@SignupActivity, "Registration success!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                            finish()

                        }else{
                            Toast.makeText(this@SignupActivity, it.exception.toString(), Toast.LENGTH_LONG).show()
                            it.exception.toString()
                        }
                    }

        }

    }
}
package com.example.wave.loginactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wave.CustomProgress
import com.example.wave.MainActivity
import com.example.wave.databinding.ActivityLoginBinding
import com.example.wave.registeractivity.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
//    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.goToRegisterButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.cardVBtn.setOnClickListener {
            val button = CustomProgress(this, it)
            button.clicked()

            val email = binding.userEmail.text.toString().trim()
            val password = binding.userPassword.text.toString()
            val condition: Boolean = checkCondition(email, password)

            if (condition) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        button.done()
                        GlobalScope.launch {
                            delay(500)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val text = "Something Went Wrong !!"
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        button.reset("Log In")
                    }
                }
            } else {
                button.reset("Log In")
            }
        }
    }

    private fun checkCondition(email: String, password: String): Boolean {

        return if (email == "" || password == "") {
            Toast.makeText(this, "Please Enter Email And Password", Toast.LENGTH_SHORT).show()
            false
        } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            Toast.makeText(this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show()
            return false
        } else {
            true
        }
    }
}
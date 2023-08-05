package com.sensomedi.matla

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.sensomedi.data.MatlaDatabase
import com.sensomedi.matla.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: MatlaDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = MatlaDatabase.getInstance(applicationContext)!!
        if (auth.currentUser != null) {
            signIn()
        }
        initView()
    }

    private fun initView() = with(binding) {

        forgotPwTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgetActivity::class.java))
        }

        loginBtn.setOnClickListener {

            if (loginEmailEt.text.isEmpty() || loginPasswordEt.text.isEmpty()) {
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(
                loginEmailEt.text.toString(),
                loginPasswordEt.text.toString(),
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signIn()
                } else {
                    Toast.makeText(this@LoginActivity, "Check email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        createAccountTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }
    }

    private fun signIn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", auth.currentUser!!.uid)
        CoroutineScope(Dispatchers.IO).launch {
            if (db.userDao().getUser().isEmpty()) {
                println("empty")
                println(db.userDao().getUser().isEmpty())
                intent.putExtra("bool", false)
                intent.putExtra("email", auth.currentUser!!.email)
                startActivity(intent)
                finish()
            } else {
                println("not empty")
                println(db.userDao().getUser().isEmpty())
                intent.putExtra("bool", true)
                intent.putExtra("email", auth.currentUser!!.email)
                startActivity(intent)
                finish()
            }
        }
    }
}
package com.sensomedi.matla

import android.R.attr.password
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import com.sensomedi.matla.databinding.ActivityForgetBinding


class ForgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "Sensomedi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        initView()
    }

    private fun initView() = with(binding) {
        resetBtn.setOnClickListener {
            if (resetEmailEt.text.toString().isEmpty()) {
                Toast.makeText(this@ForgetActivity, "Invalid email address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(resetEmailEt.text.toString(), " ")
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        try {
                            throw task.exception!!
                        } // if user enters wrong email.
                        catch (invalidEmail: FirebaseAuthInvalidUserException) {
                            Log.d(TAG, "onComplete: invalid_email")
                            Toast.makeText(
                                this@ForgetActivity,
                                "Email has not been registered. ",
                                Toast.LENGTH_SHORT
                            ).show()
                            // TODO: take your actions!
                        } // if user enters wrong password.
                        catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                            Log.d(TAG, "onComplete: wrong_password")
                            // TODO: Take your action
                            auth.sendPasswordResetEmail(resetEmailEt.text.toString())
                                .addOnCompleteListener {
                                    Toast.makeText(
                                        this@ForgetActivity,
                                        "Reset link is sent to email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                        } catch (e: Exception) {
                            Log.d(TAG, "onComplete: " + e.message)
                        }
                    }
                }
        }
    }
}
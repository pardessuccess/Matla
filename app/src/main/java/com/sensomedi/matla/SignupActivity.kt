package com.sensomedi.matla

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sensomedi.data.User
import com.sensomedi.matla.databinding.ActivitySignupBinding
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    private var gender = ""

    private val pwPatton =
        "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?.]{8,20}$"
    private val pattern = Pattern.compile(pwPatton)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        initViews()
    }

    private fun initViews() = with(binding) {
        genderGroup.setOnCheckedChangeListener { p0, p1 ->
            when (p1) {
                R.id.maleRBtn -> {
                    gender = "male"
                }
                R.id.femaleRBtn -> {
                    gender = "female"
                }
                R.id.noneRBtn -> {
                    gender = "none"
                }
            }
        }

        signUpPasswordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (!checkPassword(signUpPasswordEt.text.toString())) {
                    passwordErrorTv.text = "8 ~ 20 alphabet, number, special characters"
                } else {
                    passwordErrorTv.text = ""
                }
            }
        })

        signUpRePasswordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (signUpPasswordEt.text.toString() != signUpRePasswordEt.text.toString()) {
                    rePasswordErrorTv.text = " Password doesn't match"
                } else {
                    rePasswordErrorTv.text = ""
                }
            }
        })

        signUpBtn.setOnClickListener {

            if (signUpEmailEt.text.isEmpty() || signUpPasswordEt.text.isEmpty() || signUpHeightTv.text.isEmpty() || signUpRePasswordEt.text.isEmpty() || signUpWeightTv.text.isEmpty() || signUpAgeTv.text.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this@SignupActivity, "Please enter all information.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordErrorTv.text.isNotEmpty() || rePasswordErrorTv.text.isNotEmpty()) {
                Toast.makeText(this@SignupActivity, "Password doesn't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(signUpEmailEt.text.toString()).matches()){
                Toast.makeText(this@SignupActivity, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(
                signUpEmailEt.text.toString(),
                signUpPasswordEt.text.toString()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SignupActivity, "SignUp Complete.", Toast.LENGTH_SHORT)
                        .show()
                    createUserDB()
                    finish()
                } else if (!task.exception?.message.isNullOrEmpty()) {
                    Toast.makeText(this@SignupActivity, task.exception?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        loginTv.setOnClickListener {
            finish()
        }
    }

    private fun createUserDB() = with(binding) {
        val userId = auth.currentUser?.uid.orEmpty()
        val realtime = Firebase.database.reference.child("users").child(userId)

        realtime.setValue(
            User(
                signUpEmailEt.text.toString(),
                signUpAgeTv.text.toString().toInt(),
                gender,
                signUpHeightTv.text.toString().toInt(),
                signUpWeightTv.text.toString().toInt(),
            )
        )
    }

    private fun checkPassword(pw: String): Boolean = pattern.matcher(pw).matches()


}